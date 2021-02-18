package javatoarm.token.operator;

/**
 * Represent an assignment operator. An assignment operator can be either simple or compound.
 * A simple assignment operation simply assign the right value to the left variable.
 * A compound assignment operation first performs an arithmetic operation or an bitwise operation
 * involving both the right value and the left variable, then assign the result to the left variable.
 */
public interface AssignmentOperator extends OperatorToken.Binary {

    /**
     * Get an assignment token
     *
     * @param assignOp the operator
     * @return if the operator string is an assignment operator, returns the corresponding token.
     * Otherwise returns null.
     */
    static AssignmentOperator get(String assignOp) {
        if (assignOp.charAt(assignOp.length() - 1) == '=') {
            String op = assignOp.substring(0, assignOp.length() - 1);
            if (op.length() == 0) {
                return Simple.INSTANCE;
            } else {
                OperatorToken.Binary binaryOp = OperatorToken.Binary.get(op);
                if (binaryOp instanceof ArithmeticOperator || binaryOp instanceof Bitwise) {
                    return new Compound(binaryOp);
                }
            }
        }

        return null;
    }

    @Override
    default int getPrecedenceLevel() {
        return 1;
    }

    /**
     * Implementation of a simple assignment operator.
     * A simple assignment operation simply assign the right value to the left variable.
     */
    enum Simple implements AssignmentOperator {
        INSTANCE;

        @Override
        public String toString() {
            return "=";
        }
    }

    /**
     * Implementation of a compound assignment operator.
     * A compound assignment operation first performs an arithmetic operation or an bitwise operation
     * involving both the right value and the left variable, then assign the result to the left variable.
     */
    class Compound implements AssignmentOperator {
        private static final int HASH_CODE = 1658;

        public final OperatorToken.Binary implicitOperator;

        private Compound(OperatorToken.Binary operator) throws IllegalArgumentException {
            if (!(operator instanceof ArithmeticOperator) && !(operator instanceof Bitwise)) {
                throw new IllegalArgumentException();
            }

            this.implicitOperator = operator;
        }

        @Override
        public int hashCode() {
            return implicitOperator.hashCode() * HASH_CODE;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof Compound) {
                Compound that = (Compound) obj;
                return this.implicitOperator.equals(that.implicitOperator);
            }
            return false;
        }

        @Override
        public String toString() {
            return implicitOperator + "=";
        }
    }
}
