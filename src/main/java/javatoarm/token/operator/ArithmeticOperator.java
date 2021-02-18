package javatoarm.token.operator;

/**
 * Token representing an arithmetic operator: '+', '-', '*', '/', or '%'
 */
public interface ArithmeticOperator extends OperatorToken.Binary {

    /**
     * Get an arithmetic operator token
     *
     * @param operator the operator
     * @return if the operator string is an arithmetic operator, returns the corresponding token.
     * Otherwise returns null.
     */
    static ArithmeticOperator get(String operator) {
        if (operator.length() != 1) {
            return null;
        }
        return switch (operator.charAt(0)) {
            case '+' -> PlusMinus.PLUS;
            case '-' -> PlusMinus.MINUS;
            case '*' -> Multi.MULTIPLY;
            case '/' -> Multi.DIVIDE;
            case '%' -> Multi.MODULUS;
            default -> null;
        };
    }

    /**
     * Represent multiplicative operators: * / %
     */
    enum Multi implements ArithmeticOperator {
        MULTIPLY, DIVIDE, MODULUS;

        @Override
        public int getPrecedenceLevel() {
            return 12;
        }

        @Override
        public String toString() {
            return switch (this) {
                case MULTIPLY -> "*";
                case DIVIDE -> "/";
                case MODULUS -> "%";
            };
        }
    }

}
