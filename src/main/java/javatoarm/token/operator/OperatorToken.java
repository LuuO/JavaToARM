package javatoarm.token.operator;

import javatoarm.token.Token;

/**
 * Represent an operator in Java code. An operator can be unary, binary, or ternary (C?T:F)
 */
public interface OperatorToken extends Token {

    /**
     * Get an operator token
     *
     * @param operator the operator
     * @return if the operator string is a operator, returns the corresponding token.
     * Otherwise returns null.
     */
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

    /**
     * Represent a token of a binary operator
     */
    interface Binary extends OperatorToken {

        /**
         * Get a binary operator token
         *
         * @param op the operator
         * @return if the operator string is a binary operator, returns the corresponding token.
         * Otherwise returns null.
         */
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

    /**
     * Represent a token of an unary operator
     */
    interface Unary extends OperatorToken {

        /**
         * Get an unary operator token
         *
         * @param op the operator
         * @return if the operator string is a unary operator, returns the corresponding token.
         * Otherwise returns null.
         */
        static Unary get(String op) {
            return switch (op) {
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
