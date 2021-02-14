package javatoarm.token.operator;

public class TernaryToken implements OperatorToken {
    public static final TernaryToken QUESTION = new TernaryToken();
    public static final TernaryToken COLON = new TernaryToken();

    private TernaryToken() {
    }

    public static TernaryToken get(char c) {
        return switch (c) {
            case '?' -> QUESTION;
            case ':' -> COLON;
            default -> null;
        };
    }

    @Override
    public int getPrecedenceLevel() {
        return 2;
    }
}
