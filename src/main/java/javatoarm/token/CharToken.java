package javatoarm.token;

/**
 * Represents a comma, a semi-colon, a dot, or @
 */
public enum CharToken implements Token {
    COMMA, SEMI_COLON, DOT, AT;

    /**
     * Get a char token
     *
     * @param c the character
     * @return if the provided character is a valid CharToken, returns the corresponding token.
     * Otherwise returns null.
     */
    public static CharToken get(char c) {
        return switch (c) {
            case ';' -> SEMI_COLON;
            case ',' -> COMMA;
            case '.' -> DOT;
            case '@' -> AT;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case SEMI_COLON -> ";";
            case COMMA -> ",";
            case DOT -> ".";
            case AT -> "@";
        };
    }

}
