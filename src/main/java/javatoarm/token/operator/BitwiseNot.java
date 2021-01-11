package javatoarm.token.operator;

public class BitwiseNot implements OperatorToken.Unary {
    @Override
    public Type getUnaryOperatorType() {
        return Type.BITWISE_NOT;
    }
}
