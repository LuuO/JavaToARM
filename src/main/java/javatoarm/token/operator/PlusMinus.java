package javatoarm.token.operator;

/**
 * Represent a plus or minus sign. The meaning of the sign is context-dependent. It could be a binary
 * arithmetic operator or an unary operator.
 */
public enum PlusMinus implements ArithmeticOperator, OperatorToken.Unary {
    PLUS, MINUS;

    @Override
    public int getPrecedenceLevel() {
        return 11;
    }

    @Override
    public String toString() {
        return switch (this) {
            case PLUS -> "+";
            case MINUS -> "-";
        };
    }
}