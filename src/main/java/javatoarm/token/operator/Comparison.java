package javatoarm.token.operator;

import javatoarm.assembly.Condition;
import javatoarm.token.AngleToken;

/**
 * Represent a comparison operator token. A comparison operator compares two values and outputs one boolean value.
 */
public interface Comparison extends OperatorToken.Binary {

    /**
     * Get a comparison token
     *
     * @param operator the operator
     * @return if the operator string is a comparison operator, returns the corresponding token.
     * Otherwise returns null.
     */
    static Comparison get(String operator) {
        Condition condition;
        try {
            condition = Condition.getFromSymbol(operator);
        } catch (IllegalArgumentException e) {
            return null;
        }

        if (condition == Condition.LESS) {
            return AngleToken.LEFT;
        } else if (condition == Condition.GREATER) {
            return AngleToken.RIGHT;
        } else {
            return new Impl(condition);
        }
    }

    /**
     * Get the condition when the comparison output is true
     *
     * @return the condition
     */
    Condition getCondition();

    /**
     * The default implementation of Comparison
     */
    class Impl implements Comparison {

        private final Condition condition;

        private Impl(Condition condition) {
            if (condition == Condition.LESS || condition == Condition.GREATER) {
                throw new IllegalArgumentException("Use AngleToken");
            }

            this.condition = condition;
        }

        @Override
        public Condition getCondition() {
            return condition;
        }

        @Override
        public int getPrecedenceLevel() {
            return switch (condition) {
                case EQUAL, UNEQUAL -> 8;
                default -> 9;
            };
        }

        @Override
        public int hashCode() {
            return condition.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Comparison) {
                Comparison that = (Comparison) obj;
                return this.condition == that.getCondition();
            }
            return false;
        }

        @Override
        public String toString() {
            return condition.toSymbol();
        }
    }
}
