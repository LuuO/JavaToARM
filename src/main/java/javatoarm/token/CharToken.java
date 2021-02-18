package javatoarm.token;

/**
 * Represents a comma, a semi-colon, or a dot
 */
public enum CharToken implements Token {
    COMMA, SEMI_COLON, DOT;

    /**
     * Get a char token
     *
     * @param c the character of token
     * @return if the provided character is a valid CharToken, returns the corresponding token.
     * Otherwise returns null.
     */
    public static CharToken get(char c) {
        return switch (c) {
            case ';' -> SEMI_COLON;
            case ',' -> COMMA;
            case '.' -> DOT;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case SEMI_COLON -> ";";
            case COMMA -> ",";
            case DOT -> ".";
        };
    }

}
