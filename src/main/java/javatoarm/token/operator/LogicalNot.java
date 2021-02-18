package javatoarm.token.operator;

/**
 * Token representing a logical not symbol ('!')
 */
public enum LogicalNot implements OperatorToken.Unary {
    INSTANCE;

    @Override
    public String toString() {
        return "!";
    }
}
