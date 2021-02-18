package javatoarm.token;

/**
 * Represents a comma, a semi-colon, or a dot
 */
public class CharToken implements Token {
    public static final CharToken COMMA = new CharToken(',');
    public static final CharToken SEMI_COLON = new CharToken(';');
    public static final CharToken DOT = new CharToken('.');

    public final char c;

    private CharToken(char c) {
        this.c = c;
    }

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
    public int hashCode() {
        return c;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharToken) {
            return ((CharToken) obj).c == this.c;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(c);
    }

}
