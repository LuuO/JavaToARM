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
    default OperatorToken.Binary.Type getBinaryOperatorType() {
        return OperatorToken.Binary.Type.ASSIGNMENT;
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
        @Override
        public AssignmentOperator.Type getAssignmentOperatorType() {
            return AssignmentOperator.Type.SIMPLE;
        }
    }

    class Compound implements AssignmentOperator {
        public final OperatorToken.Binary implicitOperator;

        private Compound(OperatorToken.Binary operator) throws IllegalArgumentException {
            if (!(operator instanceof ArithmeticOperator) &&
                !(operator instanceof Bitwise)) {
                // TODO: shift
                throw new IllegalArgumentException();
            }

            this.implicitOperator = operator;
        }

        @Override
        public AssignmentOperator.Type getAssignmentOperatorType() {
            return AssignmentOperator.Type.COMPOUND;
        }
    }
}
