package javatoarm.token.operator;

/**
 * Represent a plus or minus sign. The meaning of the sign is context-dependent. It could be a binary
 * arithmetic operator or an unary operator.
 */
public enum PlusMinus implements ArithmeticOperator, OperatorToken.Unary {
    PLUS, MINUS;

    @Override
    public Unary.Type getUnaryOperatorType() {
        return switch (this) {
            case PLUS -> Unary.Type.POSITIVE;
            case MINUS -> Unary.Type.NEGATIVE;
        };
    }

    @Override
    public int getPrecedenceLevel() {
        return 11;
    }

    @Override
    public ArithmeticOperator.Type getArithmeticOperatorType() {
        return ArithmeticOperator.Type.ADDITIVE;
    }


    @Override
    public String toString() {
        return switch (this) {
            case PLUS -> "+";
            case MINUS -> "-";
        };
    }
}