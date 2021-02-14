package javatoarm.token;

import javatoarm.assembly.Condition;
import javatoarm.token.operator.Comparison;

public class AngleToken extends BracketToken implements Comparison {
    public static AngleToken LEFT = new AngleToken('<', Condition.LESS);
    public static AngleToken RIGHT = new AngleToken('>', Condition.GREATER);

    Condition condition;

    private AngleToken(char c, Condition condition) {
        super(c);
        this.condition = condition;
    }

    public static AngleToken get(Condition condition) {
        if (condition == Condition.LESS) {
            return LEFT;
        }
        if (condition == Condition.GREATER) {
            return RIGHT;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public int getPrecedenceLevel() {
        return 9;
    }

    @Override
    public int hashCode() {
        return condition.hashCode() * super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
