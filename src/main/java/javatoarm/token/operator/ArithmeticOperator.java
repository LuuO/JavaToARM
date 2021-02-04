package javatoarm.token.operator;

public interface ArithmeticOperator extends OperatorToken.Binary {

    static ArithmeticOperator get(String operator) {
        if (operator.length() != 1) {
            return null;
        }
        return switch (operator.charAt(0)) {
            case '+' -> new PlusMinus(true);
            case '-' -> new PlusMinus(false);
            case '*' -> new Multiply();
            case '/' -> new Divide();
            case '%' -> new Modulus();
            default -> null;
        };
    }

    @Override
    default OperatorToken.Binary.Type getBinaryOperatorType() {
        return OperatorToken.Binary.Type.ARITHMETIC;
    }

    @Override
    default int getPrecedenceLevel() {
        if (getArithmeticOperatorType() == Type.MULTI) {
            return 12;
        } else {
            return 11;
        }
    }

    Type getArithmeticOperatorType();

    enum Type {
        MULTI, ADDITIVE;

        public static Type get(char c) {
            return switch (c) {
                case '+', '-' -> ADDITIVE;
                case '*', '/', '%' -> MULTI;
                default -> throw new IllegalArgumentException();
            };
        }
    }

    class Multiply implements ArithmeticOperator {
        @Override
        public ArithmeticOperator.Type getArithmeticOperatorType() {
            return ArithmeticOperator.Type.MULTI;
        }
    }

    class Divide implements ArithmeticOperator {
        @Override
        public ArithmeticOperator.Type getArithmeticOperatorType() {
            return ArithmeticOperator.Type.MULTI;
        }
    }

    class Modulus implements ArithmeticOperator {
        @Override
        public ArithmeticOperator.Type getArithmeticOperatorType() {
            return ArithmeticOperator.Type.MULTI;
        }
    }

}
