package javatoarm.token.operator;

public interface AssignmentOperator extends OperatorToken.Binary {

    static AssignmentOperator get(String assignOp) {
        if (assignOp.charAt(assignOp.length() - 1) == '=') {
            String op = assignOp.substring(0, assignOp.length() - 1);
            if (op.length() == 0) {
                return new Simple();
            } else {
                OperatorToken.Binary binaryOp = OperatorToken.Binary.get(op);
                try {
                    return new Compound(binaryOp);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        return null;
    }

    @Override
    default int getPrecedenceLevel() {
        return 1;
    }

    Type getAssignmentOperatorType();

    enum Type {
        SIMPLE, COMPOUND
    }

    class Simple implements AssignmentOperator {
        private static final int HASH_CODE = 3435;

        @Override
        public AssignmentOperator.Type getAssignmentOperatorType() {
            return AssignmentOperator.Type.SIMPLE;
        }

        @Override
        public int hashCode() {
            return HASH_CODE;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Simple;
        }

        @Override
        public String toString() {
            return "=";
        }
    }

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
        public AssignmentOperator.Type getAssignmentOperatorType() {
            return AssignmentOperator.Type.COMPOUND;
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
