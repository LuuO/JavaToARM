package javatoarm.token;

public class BracketToken implements Token {
    char bracket;

    BracketToken(char c) throws IllegalArgumentException {
        bracket = c;
    }

    public static BracketToken get(char c) {
        return switch (c) {
            case '(', ')', '[', ']', '{', '}', '\'', '"' -> new BracketToken(c);
            default -> null;
        };
    }

    @Override
    public Type getTokenType() {
        return Type.BRACKET;
    }
}
