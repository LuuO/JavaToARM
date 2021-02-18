package javatoarm.token.operator;

public enum BitwiseNot implements OperatorToken.Unary {
    INSTANCE;

    @Override
    public String toString() {
        return "~";
    }
}
