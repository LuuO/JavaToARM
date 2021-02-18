package javatoarm.token.operator;

public enum LogicalNot implements OperatorToken.Unary {
    INSTANCE;

    @Override
    public String toString() {
        return "!";
    }
}
