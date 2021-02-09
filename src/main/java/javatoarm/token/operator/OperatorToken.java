package javatoarm.token.operator;

import javatoarm.token.Token;

public interface OperatorToken extends Token {

    static Token get(String operator) {
        Token token = Unary.get(operator);
        if (token == null) {
            token = Binary.get(operator);
        }
        return token;
    }

    Type getOperatorType();

    int getPrecedenceLevel();

    enum Type {
        UNARY, BINARY, PLUS_MINUS
    }

    interface Binary extends OperatorToken {

        static Binary get(String op) {
            Binary token;
            if ((token = AssignmentOperator.get(op)) != null) {
                return token;
            }
            if ((token = ArithmeticOperator.get(op)) != null) {
                return token;
            }
            if ((token = Comparison.get(op)) != null) {
                return token;
            }
            if ((token = Logical.get(op)) != null) {
                return token;
            }
            if ((token = Bitwise.get(op)) != null) {
                return token;
            }
            return null;
        }

        @Override
        default OperatorToken.Type getOperatorType() {
            return OperatorToken.Type.BINARY;
        }

        Type getBinaryOperatorType();

        enum Type {
            ASSIGNMENT, ARITHMETIC, COMPARISON, LOGICAL, BITWISE
        }
    }

    interface Unary extends OperatorToken {

        static Unary get(String operator) {
            return switch (operator) {
                case "++" -> new IncrementDecrement(true);
                case "--" -> new IncrementDecrement(false);
                case "+" -> new PlusMinus(true);
                case "-" -> new PlusMinus(false);
                case "!" -> new LogicalNot();
                case "~" -> new BitwiseNot();
                default -> null;
            };
        }

        @Override
        default OperatorToken.Type getOperatorType() {
            return OperatorToken.Type.UNARY;
        }

        @Override
        default int getPrecedenceLevel() {
            return 14;
        }

        Type getUnaryOperatorType();

        enum Type {
            INCREMENT, DECREMENT, POSITIVE, NEGATIVE, LOGICAL_NOT, BITWISE_NOT
        }
    }
}
