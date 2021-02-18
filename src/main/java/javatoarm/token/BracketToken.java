package javatoarm.token;

/**
 * Tokens that represent brackets: {}[]()
 */
public enum BracketToken implements Token {
    CURLY_L, CURLY_R, SQUARE_L, SQUARE_R, ROUND_L, ROUND_R;

    /**
     * Get a bracket token
     *
     * @param c the bracket
     * @return if the provided bracket is valid (One of "{}[]()"), returns the corresponding token.
     * Otherwise returns null.
     */
    public static BracketToken get(char c) {
        return switch (c) {
            case '(' -> ROUND_L;
            case ')' -> ROUND_R;
            case '[' -> SQUARE_L;
            case ']' -> SQUARE_R;
            case '{' -> CURLY_L;
            case '}' -> CURLY_R;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case CURLY_L -> "{";
            case CURLY_R -> "}";
            case SQUARE_L -> "[";
            case SQUARE_R -> "]";
            case ROUND_L -> "(";
            case ROUND_R -> ")";
        };
    }
}
