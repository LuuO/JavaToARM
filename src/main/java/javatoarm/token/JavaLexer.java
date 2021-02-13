package javatoarm.token;

import javatoarm.JTAException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

public class JavaLexer {
    private static final Set<Character> symbols =
        Set.of(';', '{', '}', '(', ')', '[', ']', '.', ',',
            '=', '+', '-', '*', '/', '&', '|', '%', '^', '!',
            '\'', '"', '?', ':', '<', '>', '~', '@');
    private static final Set<String> longOperators =
        Set.of("++", "--", "==", "!=", "::", "+=", "-=", "*=", "/=", "%=", "<=", ">=", "//", "/*",
            "*/", "&&", "||", "^=", "|=", "<<=", ">>=", ">>>="); // TODO: support longer operators

    private final List<String> words;
    private final Stack<Integer> checkPoints;
    private int nextIndex;

    public JavaLexer(String code) throws JTAException {
        this.nextIndex = 0;
        this.words = new ArrayList<>();
        this.checkPoints = new Stack<>();
        scan(code);
    }

    /**
     * @param c
     * @return
     */
    public static boolean isNameableChar(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
            || (c >= '0' && c <= '9') || c == '$' || c == '_';
    }

    private void scan(String code) throws JTAException.UnknownCharacter {
        State state = State.WHITESPACE;
        StringBuilder word = new StringBuilder();
        char[] charArray = code.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            switch (state) {
                // TODO: add char, @, \n support
                case WHITESPACE: /* last char is whitespace */
                    if (Character.isWhitespace(c)) {
                        continue;
                    } else {
                        state = findNextState(word, c);
                    }
                    break;
                case NAME: /* a word (int, String, class, className, 100, ...) */
                    if (isNameableChar(c)) {
                        word.append(c);
                    } else {
                        collectAndClear(word);
                        state = findNextState(word, c);
                    }
                    break;
                case SYMBOL: /* last char is a symbol (+, ], }, =, ...) */
                    word.append(c);
                    String combinedSymbol = wordSymbol(word, charArray, i);
                    i += combinedSymbol.length() - 1;
                    if (combinedSymbol.equals("/*")) {
                        state = State.COMMENT_ML;
                        if (c == '*') {
                            word.append(c);
                        }
                    } else if (combinedSymbol.equals("//")) {
                        if (c == '\n') {
                            state = State.WHITESPACE;
                        } else {
                            state = State.COMMENT_SL;
                        }
                    } else {
                        words.add(combinedSymbol);
                        state = findNextState(word, charArray[i]);
                    }
                    break;
                case COMMENT_SL: /* in a single-line comment */
                    if (c == '\n') {
                        state = State.WHITESPACE;
                    }
                    break;
                case COMMENT_ML: /* in a multi-line comment */
                    String currentWord = word.toString();
                    word.setLength(0);
                    if (c == '/' && currentWord.equals("*")) {
                        state = State.WHITESPACE;
                    } else if (c == '*') {
                        word.append(c);
                    }
                    break;
                case STRING:
                    boolean escaped = putChar(word, c);
                    if (!escaped && c == '"') {
                        collectAndClear(word);
                        state = State.WHITESPACE;
                    } else {
                        word.append(c);
                    }
                    break;
                case CHAR:
                    throw new UnsupportedOperationException();
            }

        }

        if (word.length() > 0) {
            words.add(word.toString());
        }

    }

    private String wordSymbol(StringBuilder builder, char[] charArray, int nextIndex) {
        char nextChar = charArray[nextIndex];
        while (symbols.contains(nextChar) && builder.length() < 4) {
            builder.append(nextChar);
            nextIndex++;
            if (nextIndex < charArray.length) {
                nextChar = charArray[nextIndex];
            } else {
                break;
            }
        }
        for (int i = builder.length(); i > 1; i--) {
            String subString = builder.substring(0, i);
            if (longOperators.contains(subString)) {
                builder.setLength(0);
                return subString;
            }
        }
        String symbol = builder.substring(0, 1);
        builder.setLength(0);
        return symbol;
    }

    private State findNextState(StringBuilder word, char c) throws JTAException.UnknownCharacter {
        State state;
        if (Character.isWhitespace(c)) {
            state = State.WHITESPACE;
        } else if (isNameableChar(c)) {
            state = State.NAME;
            word.append(c);
        } else if (symbols.contains(c)) {
            state = State.SYMBOL;
            word.append(c);
        } else if (c == '"') {
            state = State.STRING;
            word.append(c);
        } else if (c == '\'') {
            state = State.CHAR;
            word.append(c);
        } else {
            throw new JTAException.UnknownCharacter(c);
        }
        return state;
    }

    private boolean putChar(StringBuilder word, char c) throws JTAException.UnknownCharacter {
        int lastIndex = word.length() - 1;
        boolean escaped;
        if (!word.isEmpty() && word.charAt(lastIndex) == '\\') {
            word.deleteCharAt(lastIndex);
            switch (c) {
                case 'b' -> c = '\b';
                case 'f' -> c = '\f';
                case 'n' -> c = '\n';
                case 'r' -> c = '\r';
                case 't' -> c = '\t';
                case '\'', '\"', '\\' -> {/* do nothing */}
                default -> throw new JTAException.UnknownCharacter("\\" + c);
            }
            escaped = true;
        } else {
            escaped = false;
        }
        word.append(c);
        return escaped;
    }

    private void collectAndClear(StringBuilder builder) {
        words.add(builder.toString());
        builder.setLength(0);
    }

    private Token getNextToken() throws JTAException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        String word = words.get(nextIndex);
        return Token.getObject(word);
    }

    public boolean hasNext() {
        return nextIndex < words.size();
    }

    public Token next() throws JTAException {
        Token token = getNextToken();
        nextIndex += 1;
        return token;
    }

    public Token peek() throws JTAException {
        return getNextToken();
    }

    public void rewind(int steps) {
        if (steps > nextIndex) {
            throw new IllegalArgumentException();
        }
        nextIndex -= steps;
    }

    public void rewind() {
        rewind(1);
    }

    public void createCheckPoint() {
        checkPoints.push(nextIndex);
    }

    public void returnToLastCheckPoint() {
        nextIndex = checkPoints.pop();
    }

    public void next(Token expected) throws JTAException {
        Token next = getNextToken();
        nextIndex += 1;
        if (!expected.equals(next)) {
            throw new JTAException.UnexpectedToken(expected, next);
        }
    }

    public Token next(Class<?> expected) throws JTAException {
        Token next = getNextToken();
        nextIndex += 1;
        if (next.getClass() != expected) {
            throw new JTAException.UnexpectedToken(expected.toString(), next);
        } else {
            return next;
        }
    }

    public boolean nextIf(Token target) throws JTAException {
        Token next = getNextToken();
        if (next.equals(target)) {
            nextIndex += 1;
            return true;
        } else {
            return false;
        }
    }

    public boolean nextIf(KeywordToken.Keyword keyword) throws JTAException {
        Token next = getNextToken();
        if (next instanceof KeywordToken && ((KeywordToken) next).keyword == keyword) {
            nextIndex += 1;
            return true;
        } else {
            return false;
        }
    }

    enum State {
        WHITESPACE, NAME, SYMBOL, COMMENT_SL, COMMENT_ML, STRING, CHAR
    }
}
