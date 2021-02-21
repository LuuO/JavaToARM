package javatoarm.token;

import javatoarm.JTAException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * A tokenizer that breaks Java code into tokens
 */
public class JavaLexer {
    private static final Set<Character> symbols =
            Set.of(';', '{', '}', '(', ')', '[', ']', '.', ',',
                    '=', '+', '-', '*', '/', '&', '|', '%', '^', '!',
                    '\'', '"', '?', ':', '<', '>', '~', '@', '\\');
    private static final Set<String> longOperators =
            Set.of("++", "--", "==", "!=", "::", "+=", "-=", "*=", "/=",
                    "%=", "<=", ">=", "//", "/*", "*/", "&&", "||", ">>",
                    "<<", ">>>", "^=", "|=", "<<=", ">>=", ">>>=", "->");

    private final List<String> words;
    private final Stack<Integer> checkPoints;
    private int nextIndex;

    /**
     * Create a lexer with the provided Java code
     *
     * @param code the Java code
     * @throws JTAException if an error occurs
     */
    public JavaLexer(String code) throws JTAException {
        this.nextIndex = 0;
        this.words = new ArrayList<>();
        this.checkPoints = new Stack<>();
        scan(code);
    }

    /**
     * Check if the character can appear in a name in Java.
     *
     * @param c the character
     * @return true if it can appear in a name in Java, false otherwise.
     */
    public static boolean isNameableChar(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9') || c == '$' || c == '_';
    }

    /**
     * Check if there is still tokens left.
     *
     * @return true if there is still tokens left, false otherwise.
     */
    public boolean hasNext() {
        return nextIndex < words.size();
    }

    /**
     * Get and eat the next token
     *
     * @return the next token
     * @throws JTAException if an error occurs
     */
    public Token next() throws JTAException {
        Token token = getNextToken();
        nextIndex += 1;
        return token;
    }

    /**
     * Eat next token and ensures it is the same as the expected one
     *
     * @param expected the expected token
     * @throws JTAException.UnexpectedToken if next token is not the same as the expected one
     * @throws JTAException                 if an error occurs
     */
    public void next(Token expected) throws JTAException {
        Token next = getNextToken();
        nextIndex += 1;
        if (!expected.equals(next)) {
            throw new JTAException.UnexpectedToken(expected, next);
        }
    }

    /**
     * Get next token and ensures it is of some certain type
     *
     * @param expected the expected type
     * @return next token
     * @throws JTAException.UnexpectedToken if next token is not of the expected type
     * @throws JTAException                 if an error occurs
     */
    public Token next(Class<?> expected) throws JTAException {
        Token next = getNextToken();
        nextIndex += 1;
        if (!expected.isAssignableFrom(next.getClass())) {
            throw new JTAException.UnexpectedToken(expected.toString(), next);
        } else {
            return next;
        }
    }

    /**
     * Eat next token if it is the same as the target one
     *
     * @param target the target token
     * @return true if the next token is the same as the target one, false otherwise
     * @throws JTAException if an error occurs
     */
    public boolean nextIf(Token target) throws JTAException {
        Token next = getNextToken();
        if (next.equals(target)) {
            nextIndex += 1;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Eat next token and ensures it is of some certain type
     *
     * @param target the target type
     * @return true if the next token is of the target type, false otherwise
     * @throws JTAException.UnexpectedToken if next token is not of the expected type
     * @throws JTAException                 if an error occurs
     */
    public boolean nextIf(Class<?> target) throws JTAException {
        Token next = getNextToken();
        if (target.isAssignableFrom(next.getClass())) {
            nextIndex += 1;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the next token but does not eat it
     *
     * @return the next token
     * @throws JTAException if an error occurs
     */
    public Token peek() throws JTAException {
        return getNextToken();
    }

    /**
     * Check if next token equals to the expected token
     *
     * @return the expected token
     * @throws JTAException if an error occurs
     */
    public boolean peek(Token expected) throws JTAException {
        return getNextToken().equals(expected);
    }

    /**
     * Uneat tokens
     *
     * @param steps number of tokens to uneat
     */
    public void rewind(int steps) {
        if (steps > nextIndex) {
            throw new IllegalArgumentException();
        }
        nextIndex -= steps;
    }

    /**
     * Uneat 1 token
     */
    public void rewind() {
        rewind(1);
    }

    /**
     * Create a check point at current location
     */
    public void createCheckPoint() {
        checkPoints.push(nextIndex);
    }

    /**
     * Return to the last created check point and delete it.
     */
    public void returnToLastCheckPoint() {
        nextIndex = checkPoints.pop();
    }

    /**
     * Delete the last created check point.
     */
    public void deleteLastCheckPoint() {
        checkPoints.pop();
    }

    private void scan(String code) throws JTAException.UnknownCharacter {
        State state = State.WHITESPACE;
        StringBuilder word = new StringBuilder();
        char[] charArray = code.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            switch (state) {
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
                    // TODO: simplify
                    if (word.length() == 0) {
                        state = findNextState(word, c);
                        break;
                    }
                    String combinedSymbol = wordSymbol(word, charArray, i);
                    i += combinedSymbol.length() - 2;
                    switch (combinedSymbol) {
                        case "/*" -> state = State.COMMENT_ML;
                        case "//" -> state = State.COMMENT_SL;
                        case "\"" -> {
                            word.append(combinedSymbol);
                            state = State.STRING;
                        }
                        default -> words.add(combinedSymbol);
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
                    if (c == '\\') {
                        state = State.STRING_ESCAPE;
                    } else {
                        word.append(c);
                        if (c == '"') {
                            collectAndClear(word);
                            state = State.WHITESPACE;
                        }
                    }
                    break;
                case STRING_ESCAPE:
                    word.append(escapeCharOf(c));
                    state = State.STRING;
                    break;
                case CHAR:
                    if (c == '\\') {
                        c = escapeCharOf(charArray[++i]);
                        word.append(c);
                        if (word.length() >= 3) {
                            throw new JTAException.UnknownCharacter(
                                    "Not a char: %s, last char is escaped".formatted(word));
                        }
                    } else {
                        word.append(c);
                        if (word.length() == 3) {
                            if (word.charAt(0) == '\'' && word.charAt(2) == '\'') {
                                collectAndClear(word);
                                state = State.WHITESPACE;
                            } else {
                                throw new JTAException.UnknownCharacter(
                                        "Not a char: %s".formatted(word));
                            }
                        }
                    }
                    break;
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
        for (int i = builder.length(); i > 0; i--) {
            String subString = builder.substring(0, i);
            if (i == 1 || longOperators.contains(subString)) {
                builder.setLength(0);
                return subString;
            }
        }
        throw new AssertionError();
    }

    private State findNextState(StringBuilder word, char c) throws JTAException.UnknownCharacter {
        State state;
        if (Character.isWhitespace(c)) {
            state = State.WHITESPACE;
        } else if (isNameableChar(c)) {
            state = State.NAME;
            word.append(c);
        } else if (c == '"') {
            state = State.STRING;
            word.append(c);
        } else if (c == '\'') {
            state = State.CHAR;
            word.append(c);
        } else if (symbols.contains(c)) {
            state = State.SYMBOL;
            word.append(c);
        } else {
            throw new JTAException.UnknownCharacter(c);
        }
        return state;
    }

    private char escapeCharOf(char c) throws JTAException.UnknownCharacter {
        return switch (c) {
            case 'b' -> '\b';
            case 'f' -> '\f';
            case 'n' -> '\n';
            case 'r' -> '\r';
            case 't' -> '\t';
            case '0' -> '\0';
            case '\'', '\"', '\\' -> c;
            default -> throw new JTAException.UnknownCharacter("\\" + c);
        };
    }

    private void collectAndClear(StringBuilder builder) {
        words.add(builder.toString());
        builder.setLength(0);
    }

    private Token getNextToken() throws JTAException {
        if (!hasNext()) {
            throw new JTAException("Unexpected End of File");
        }
        String word = words.get(nextIndex);
        return Token.getObject(word);
    }

    /**
     * States during scanning process
     */
    private enum State {
        WHITESPACE, NAME, SYMBOL, COMMENT_SL, COMMENT_ML, STRING, STRING_ESCAPE, CHAR
    }
}
