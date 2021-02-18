package javatoarm.token.operator;

public interface ArithmeticOperator extends OperatorToken.Binary {

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
