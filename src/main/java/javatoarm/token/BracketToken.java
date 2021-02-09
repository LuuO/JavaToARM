package javatoarm.token;

public class BracketToken implements Token {
    public static BracketToken CURLY_L = new BracketToken('{');
    public static BracketToken CURLY_R = new BracketToken('}');
    public static BracketToken SQUARE_L = new BracketToken('[');
    public static BracketToken SQUARE_R = new BracketToken(']');
    public static BracketToken ROUND_L = new BracketToken('(');
    public static BracketToken ROUND_R = new BracketToken(')');

    char bracket;

    public BracketToken(char c) throws IllegalArgumentException {
        bracket = c;
    }

    public static BracketToken get(char c) {
        return switch (c) {
            case '(', ')', '[', ']', '{', '}', '\'', '"' -> new BracketToken(c);
            default -> null;
        };
    }

    @Override
    public int hashCode() {
        return bracket;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BracketToken) {
            BracketToken that = (BracketToken) obj;
            return this.bracket == that.bracket;
        }
        return false;
    }
}
