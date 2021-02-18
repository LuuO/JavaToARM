package javatoarm.token;

/**
 * Token that represent an arrow "->"
 */
public enum ArrowToken implements Token {
    INSTANCE;

    /**
     * Get an arrow token
     *
     * @param s a string
     * @return if s is "->", returns ArrowToken.INSTANCE. Otherwise, returns null
     */
    public static ArrowToken get(String s) {
        return s.equals("->") ? INSTANCE : null;
    }

    @Override
    public String toString() {
        return "->";
    }
}
