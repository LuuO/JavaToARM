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
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    default OperatorToken.Binary.Type getBinaryOperatorType() {
        return OperatorToken.Binary.Type.ARITHMETIC;
    }

    Type getArithmeticOperatorType();

    enum Type {
        ADD, SUB, MUL, DIV, MOD;

        public static Type get(char c) {
            return switch (c) {
                case '+' -> ADD;
                case '-' -> SUB;
                case '*' -> MUL;
                case '/' -> DIV;
                case '%' -> MOD;
                default -> throw new IllegalArgumentException();
            };
        }
    }

    class Multiply implements ArithmeticOperator {
        @Override
        public ArithmeticOperator.Type getArithmeticOperatorType() {
            return ArithmeticOperator.Type.MUL;
        }
    }

    class Divide implements ArithmeticOperator {
        @Override
        public ArithmeticOperator.Type getArithmeticOperatorType() {
            return ArithmeticOperator.Type.DIV;
        }
    }

    class Modulus implements ArithmeticOperator {
        @Override
        public ArithmeticOperator.Type getArithmeticOperatorType() {
            return ArithmeticOperator.Type.MOD;
        }
    }

}
