package javatoarm.token.operator;

//TODO: subtype assignment operator
public class IncrementDecrement implements OperatorToken.Unary {
    public final boolean isIncrement;

    public IncrementDecrement(boolean isIncrement) {
        this.isIncrement = isIncrement;
    }

    @Override
    public Type getUnaryOperatorType() {
        return isIncrement ? Type.INCREMENT : Type.DECREMENT;
    }
}
