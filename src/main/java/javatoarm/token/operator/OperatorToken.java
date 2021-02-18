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

    /**
     * Get the precedence of the operator
     *
     * @return precedence of the operator
     */
    int getPrecedenceLevel();

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
    }

    interface Unary extends OperatorToken {

        static Unary get(String operator) {
            return switch (operator) {
                case "++" -> IncrementDecrement.INCREMENT;
                case "--" -> IncrementDecrement.DECREMENT;
                case "+" -> PlusMinus.PLUS;
                case "-" -> PlusMinus.MINUS;
                case "!" -> LogicalNot.INSTANCE;
                case "~" -> BitwiseNot.INSTANCE;
                default -> null;
            };
        }

        @Override
        default int getPrecedenceLevel() {
            return 14;
        }

    }
}
