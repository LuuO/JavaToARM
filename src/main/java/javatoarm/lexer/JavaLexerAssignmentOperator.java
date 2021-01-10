package javatoarm.lexer;

public class JavaLexerAssignmentOperator extends JavaLexerBinaryOperator {
    private final Type assignmentOperatorType;
    private final JavaLexerBinaryOperator implicitOperator;

    private JavaLexerAssignmentOperator() {
        super(JavaLexerBinaryOperator.Type.ASSIGNMENT);
        this.assignmentOperatorType = Type.SIMPLE;
        this.implicitOperator = null;
    }

    private JavaLexerAssignmentOperator(JavaLexerBinaryOperator operator)
        throws IllegalArgumentException {

        super(JavaLexerBinaryOperator.Type.ASSIGNMENT);
        this.assignmentOperatorType = Type.COMPOUND;
        this.implicitOperator = operator;

        if (!(operator instanceof JavaLexerArithmeticOperator) &&
            !(operator instanceof JavaLexerBitwiseOperator)) {
            throw new IllegalArgumentException();
        }
    }

    public static JavaLexerAssignmentOperator get(String assignOp) {
        if (assignOp.charAt(assignOp.length() - 1) == '=') {
            String op = assignOp.substring(0, assignOp.length() - 1);
            if (op.length() == 0) {
                return new JavaLexerAssignmentOperator();
            } else {
                JavaLexerBinaryOperator binaryOp = JavaLexerBinaryOperator.get(op);
                try {
                    return new JavaLexerAssignmentOperator(binaryOp);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        return null;
    }

    enum Type {
        SIMPLE, COMPOUND
    }
}
