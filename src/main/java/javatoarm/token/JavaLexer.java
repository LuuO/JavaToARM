package javatoarm.token;

import javatoarm.JTAException;

import java.util.*;

public class JavaLexer implements Iterator<Token> {
    private static final Set<Character> symbols =
            Set.of(';', '{', '}', '(', ')', '[', ']', '.', ',',
                    '=', '+', '-', '*', '/', '&', '|', '%', '^', '!',
                    '\'', '"', '?', ':', '<', '>', '~');
    private static final Set<String> longOperators =
            Set.of("++", "--", "==", "!=", "::", "+=", "-=", "*=", "/=", "%=", "<=", ">=", "//", "/*",
                    "*/"); // TODO: support longer operators

    private final List<String> words;
    private final Stack<Integer> checkPoints;
    private int nextIndex;

    public JavaLexer(String code) throws JTAException {
        this.nextIndex = 0;
        this.words = scan(code);
        this.checkPoints = new Stack<>();
    }

    /**
     * @param c
     * @return
     */
    public static boolean isNameableChar(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9') || c == '$' || c == '_';
    }

    private List<String> scan(String code) throws JTAException.UnknownCharacter {
        State state = State.WHITESPACE;
        List<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (char c : code.toCharArray()) {
            switch (state) {
                // TODO: add string support
                case WHITESPACE: /* last char is whitespace */
                    if (Character.isWhitespace(c)) {
                        continue;
                    } else if (isNameableChar(c)) {
                        state = State.NAME;
                        word.append(c);
                    } else if (symbols.contains(c)) {
                        state = State.SYMBOL;
                        word.append(c);
                    } else if (c == '"') {
                        state = State.STRING;
                        word.append(c);
                    } else {
                        throw new JTAException.UnknownCharacter(c);
                    }
                    break;
                case NAME: /* a word (int, String, class, className, 100, ...) */
                    if (isNameableChar(c)) {
                        word.append(c);
                    } else {
                        collectAndClear(word, words);
                        if (Character.isWhitespace(c)) {
                            state = State.WHITESPACE;
                        } else if (symbols.contains(c)) {
                            state = State.SYMBOL;
                            word.append(c);
                        } else if (c == '"') {
                            state = State.STRING;
                            word.append(c);
                        } else {
                            throw new JTAException.UnknownCharacter(c);
                        }
                    }
                    break;
                case SYMBOL: /* last char is a symbol (+, ], }, =, ...) */
                    String combinedSymbol = word.toString() + c;
                    if (combinedSymbol.equals("/*")) {
                        state = State.COMMENT_ML;
                        word.setLength(0);
                    } else if (combinedSymbol.equals("//")) {
                        state = State.COMMENT_SL;
                        word.setLength(0);
                    } else if (Character.isWhitespace(c)) {
                        state = State.WHITESPACE;
                        collectAndClear(word, words);
                    } else if (isNameableChar(c)) {
                        state = State.NAME;
                        collectAndClear(word, words);
                        word.append(c);
                    } else if (c == '"') {
                        state = State.STRING;
                        collectAndClear(word, words);
                        word.append(c);
                    } else if (symbols.contains(c)) {
                        if (word.length() == 2) { /* word contains a long operator */
                            collectAndClear(word, words);
                        } else {
                            assert word.length() == 1;
                            if (!longOperators.contains(combinedSymbol)) {
                                collectAndClear(word, words);
                            }
                        }
                        word.append(c);
                    } else {
                        throw new JTAException.UnknownCharacter(c);
                    }
                    break;
                case COMMENT_SL: /* in a single-line comment */
                    if (c == '\n') {
                        state = State.WHITESPACE;
                    }
                    break;
                case COMMENT_ML: /* in a multi-line comment */
                    if (c == '/') {
                        if (word.toString().equals("*")) {
                            word.setLength(0);
                            state = State.WHITESPACE;
                        }
                    } else if (c == '*') {
                        word.setLength(0);
                        word.append(c);
                    } else {
                        word.setLength(0);
                    }
                    break;
                case STRING:
                    if (c == '"') {
                        word.append(c);
                        collectAndClear(word, words);
                        state = State.WHITESPACE;
                    } else {
                        word.append(c);
                    }
                    break;
            }

        }

        if (word.length() > 0) {
            words.add(word.toString());
        }

        return words;
    }

    private void collectAndClear(StringBuilder builder, List<String> list) {
        list.add(builder.toString());
        builder.setLength(0);
    }

    private Token getNextToken() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        String word = words.get(nextIndex);
        return Token.getObject(word);
    }

    @Override
    public boolean hasNext() {
        return nextIndex < words.size();
    }

    @Override
    public Token next() {
        Token token = getNextToken();
        nextIndex += 1;
        return token;
    }

    public Token peek() {
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

    public void next(Token expected) throws JTAException.UnexpectedToken {
        Token next = getNextToken();
        nextIndex += 1;
        if (!expected.equals(next)) {
            throw new JTAException.UnexpectedToken(expected, next);
        }
    }

    public Token next(Class<?> expected) throws JTAException.UnexpectedToken {
        Token next = getNextToken();
        nextIndex += 1;
        if (next.getClass() != expected) {
            throw new JTAException.UnexpectedToken(expected.toString(), next);
        } else {
            return next;
        }
    }

    public boolean nextIf(Token target) {
        Token next = getNextToken();
        if (next.equals(target)) {
            nextIndex += 1;
            return true;
        } else {
            return false;
        }
    }

    public boolean nextIf(KeywordToken.Keyword keyword) {
        Token next = getNextToken();
        if (next instanceof KeywordToken && ((KeywordToken) next).keyword == keyword) {
            nextIndex += 1;
            return true;
        } else {
            return false;
        }
    }

    enum State {
        WHITESPACE, NAME, SYMBOL, COMMENT_SL, COMMENT_ML, STRING
    }
}
