package javatoarm.token.operator;

/**
 * Represent a bitwise not operator token
 */
public enum BitwiseNot implements OperatorToken.Unary {
    INSTANCE;

    @Override
    public String toString() {
        return "~";
    }
}
