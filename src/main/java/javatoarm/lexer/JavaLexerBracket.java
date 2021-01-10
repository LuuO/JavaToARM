package javatoarm.lexer;

public class JavaLexerBracket extends JavaLexerToken {
    char bracket;

    JavaLexerBracket(char c) throws IllegalArgumentException {
        super(Type.BRACKET);
        bracket = c;
    }

    public static JavaLexerBracket get(char c) {
        return switch (c) {
            case '(', ')', '[', ']', '{', '}', '\'', '"' -> new JavaLexerBracket(c);
            default -> null;
        };
    }
}
