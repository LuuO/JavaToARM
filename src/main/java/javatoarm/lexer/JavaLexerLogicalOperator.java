package javatoarm.lexer;

public class JavaLexerLogicalOperator extends JavaLexerBinaryOperator {
    private final Type logicalOperatorType;

    private JavaLexerLogicalOperator(Type type) {
        super(JavaLexerBinaryOperator.Type.LOGICAL);
        this.logicalOperatorType = type;
    }

    public static JavaLexerLogicalOperator get(String operator) {
        try {
            return new JavaLexerLogicalOperator(Type.get(operator));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    enum Type {
        AND, OR;

        public static Type get(String op) {
            return switch (op) {
                case "&&" -> AND;
                case "||" -> OR;

                default -> throw new IllegalArgumentException();
            };
        }
    }
}
