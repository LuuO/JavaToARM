package javatoarm.token.operator;

public class PlusMinus implements ArithmeticOperator, OperatorToken.Unary {
    public final boolean isPlus;

    public PlusMinus(boolean isPlus) {
        this.isPlus = isPlus;
    }

    @Override
    public OperatorToken.Type getOperatorType() {
        return OperatorToken.Type.PLUS_MINUS;
    }

    @Override
    public Unary.Type getUnaryOperatorType() {
        return isPlus ? Unary.Type.POSITIVE : Unary.Type.NEGATIVE;
    }

    @Override
    public ArithmeticOperator.Type getArithmeticOperatorType() {
        return isPlus ? ArithmeticOperator.Type.ADD : ArithmeticOperator.Type.SUB;
    }

    @Override
    public int hashCode() {
        return isPlus ? 1 : 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlusMinus) {
            return ((PlusMinus) obj).isPlus == this.isPlus;
        }
        return false;
    }
}