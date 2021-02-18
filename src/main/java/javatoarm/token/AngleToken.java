package javatoarm.token;

import javatoarm.assembly.Condition;
import javatoarm.token.operator.Comparison;

/**
 * Token representing < or >
 */
public enum AngleToken implements Comparison {
    LEFT, RIGHT;

    /**
     * Get an angle token
     *
     * @param c the character
     * @return if the provided character is < or >, returns the corresponding token.
     * Otherwise returns null.
     */
    public static AngleToken get(char c) {
        return switch (c) {
            case '<' -> LEFT;
            case '>' -> RIGHT;
            default -> null;
        };
    }

    @Override
    public Condition getCondition() {
        return switch (this) {
            case LEFT -> Condition.LESS;
            case RIGHT -> Condition.GREATER;
        };
    }

    @Override
    public int getPrecedenceLevel() {
        return 9;
    }

    @Override
    public String toString() {
        return switch (this) {
            case LEFT -> "<";
            case RIGHT ->">";
        };
    }
}
