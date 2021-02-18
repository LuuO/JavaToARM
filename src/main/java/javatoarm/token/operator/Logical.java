package javatoarm.token.operator;

/**
 * Token representing an AND or OR logical symbol
 */
public enum Logical implements OperatorToken.Binary {
    AND, OR;

    /**
     * Get an Logical token
     *
     * @param operator the operator
     * @return if the operator string is a logical operator, returns the corresponding token.
     * Otherwise returns null.
     */
    public static Logical get(String operator) {
        return switch (operator) {
            case "&&" -> AND;
            case "||" -> OR;
            default -> null;
        };
    }

    @Override
    public int getPrecedenceLevel() {
        return switch (this) {
            case AND -> 4;
            case OR -> 3;
        };
    }

}
