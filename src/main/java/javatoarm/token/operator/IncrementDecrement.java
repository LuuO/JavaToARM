package javatoarm.token.operator;

/**
 * Represent a increment of decrement operator token (++ or --)
 */
public enum IncrementDecrement implements OperatorToken.Unary, AssignmentOperator {
    INCREMENT, DECREMENT;

    @Override
    public int getPrecedenceLevel() {
        return 15; // 15 for post, 14 for pre
    }

    @Override
    public String toString() {
        return switch (this) {
            case INCREMENT -> "++";
            case DECREMENT -> "--";
        };
    }
}
