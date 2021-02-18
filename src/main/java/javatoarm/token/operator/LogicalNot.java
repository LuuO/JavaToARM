package javatoarm.token.operator;

public class LogicalNot implements OperatorToken.Unary {
    @Override
    public Type getUnaryOperatorType() {
        return Type.LOGICAL_NOT;
    }

    @Override
    public String toString() {
        return "!";
    }
}
