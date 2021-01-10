package javatoarm.lexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JavaLexer implements Iterator<JavaLexerToken> {
    private static final Set<Character> symbols =
        Set.of(';', '{', '}', '(', ')', '[', ']', '.', ',',
            '=', '+', '-', '*', '/', '&', '|', '%', '^', '!',
            '\'', '"', '?', ':', '<', '>', '~');
    private static final Set<String> longOperators =
        Set.of("++", "--", "==", "!=", "::", "+=", "-=", "*=", "/=", "%=", "<=", ">=", "//", "/*",
            "*/");
    private final List<String> words;
    private int index;

    public JavaLexer(String code) throws JavaLexerException {
        this.index = 0;
        this.words = breakdown(code);
    }

    public static boolean isNameableChar(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
            || (c >= '0' && c <= '9') || c == '$' || c == '_';
    }

    private List<String> breakdown(String code) throws JavaLexerUnknownCharacterException {
        State state = State.WHITESPACE;
        List<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (char c : code.toCharArray()) {
            switch (state) {
                case WHITESPACE: /* last char is whitespace */
                    if (Character.isWhitespace(c)) {
                        continue;
                    } else if (isNameableChar(c)) {
                        state = State.NAME;
                        word.append(c);
                    } else if (symbols.contains(c)) {
                        state = State.SYMBOL;
                        word.append(c);
                    } else {
                        throw new JavaLexerUnknownCharacterException();
                    }
                    break;
                case NAME: /* a word (int, String, class, className, 100, ...) */
                    if (Character.isWhitespace(c)) {
                        state = State.WHITESPACE;
                        collectAndClear(word, words);
                    } else if (isNameableChar(c)) {
                        word.append(c);
                    } else if (symbols.contains(c)) {
                        state = State.SYMBOL;
                        collectAndClear(word, words);
                        word.append(c);
                    } else {
                        throw new JavaLexerUnknownCharacterException();
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
                        throw new JavaLexerUnknownCharacterException();
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

    public JavaLexerToken next() {
        String word = words.get(index++);
        return JavaLexerToken.getObject(word);
    }

    public boolean hasNext() {
        return index < words.size();
    }

    enum State {
        WHITESPACE, NAME, SYMBOL, COMMENT_SL, COMMENT_ML
    }
}
