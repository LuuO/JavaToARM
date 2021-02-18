package javatoarm.token;

/**
 * Represents a comma, a semi-colon, a dot, a question mark, or a colon
 */
public class CharToken implements Token {
    public static final CharToken COMMA = new CharToken(',');
    public static final CharToken SEMI_COLON = new CharToken(';');
    public static final CharToken DOT = new CharToken('.');
    public static final CharToken QUESTION = new CharToken('?');
    public static final CharToken COLON = new CharToken(':');

    public final char c;

    private CharToken(char c) {
        this.c = c;
    }

    public static CharToken get(char c) {
        return switch (c) {
            case ';', ',', '.' -> new CharToken(c);
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
