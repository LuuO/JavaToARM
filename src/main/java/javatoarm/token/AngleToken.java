package javatoarm.token;

import javatoarm.assembly.Condition;
import javatoarm.token.operator.Comparison;

public enum AngleToken implements Comparison {
    LEFT, RIGHT;

    public static AngleToken get(Condition condition) {
        return switch (condition) {
            case LESS -> LEFT;
            case GREATER -> RIGHT;
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
