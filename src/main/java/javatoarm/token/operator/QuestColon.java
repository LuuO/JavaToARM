package javatoarm.token.operator;

/**
 * Represent a question mark or a colon.
 */
public class QuestColon implements OperatorToken {
    public static final QuestColon QUESTION = new QuestColon('?');
    public static final QuestColon COLON = new QuestColon(':');

    public final char c;

    private QuestColon(char c) {
        this.c = c;
    }

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
}
