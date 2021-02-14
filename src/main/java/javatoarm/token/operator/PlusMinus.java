package javatoarm.token.operator;

public class PlusMinus implements ArithmeticOperator, OperatorToken.Unary {
    public final boolean isPlus;

    public PlusMinus(boolean isPlus) {
        this.isPlus = isPlus;
    }

    @Override
    public Unary.Type getUnaryOperatorType() {
        return isPlus ? Unary.Type.POSITIVE : Unary.Type.NEGATIVE;
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