package javatoarm.token.operator;

/**
 * Represents bit-level operators.
 */
public enum Bitwise implements OperatorToken.Binary {
    AND, OR, XOR, L_SHIFT, SIGNED_R_SHIFT, ZEROFILL_R_SHIFT;

    /**
     * Get an instances of bit-level operator
     *
     * @param operator the operator string
     * @return if the operator string is a bit-level operator, returns the corresponding operator.
     * Otherwise, returns null.
     */
    public static Bitwise get(String operator) {
        return switch (operator) {
            case "&" -> AND;
            case "|" -> OR;
            case "^" -> XOR;
            case "<<" -> L_SHIFT;
            case ">>" -> SIGNED_R_SHIFT;
            case ">>>" -> ZEROFILL_R_SHIFT;

            default -> null;
        };
    }

    @Override
    public int getPrecedenceLevel() {
        return switch (this) {
            case AND -> 7;
            case XOR -> 6;
            case OR -> 5;
            default -> 10;
        };
    }

}
