package javatoarm.token.operator;

/**
 * Represent a question mark or a colon.
 */
public enum QuestColon implements OperatorToken {
    QUESTION, COLON;

    /**
     * Get a QuestColon token
     *
     * @param c the character
     * @return if the provided character is '?' or ':', returns the corresponding token.
     * Otherwise returns null.
     */
    public static QuestColon get(char c) {
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

    @Override
    public String toString() {
        return switch (this) {
            case QUESTION -> "?";
            case COLON -> ":";
        };
    }
}
