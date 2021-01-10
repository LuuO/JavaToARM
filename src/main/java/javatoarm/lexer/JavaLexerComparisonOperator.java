package javatoarm.lexer;

public class JavaLexerComparisonOperator extends JavaLexerBinaryOperator {
    private final Type comparisonOperatorType;

    private JavaLexerComparisonOperator(Type type) {
        super(JavaLexerBinaryOperator.Type.COMPARISON);
        this.comparisonOperatorType = type;
    }

    public static JavaLexerComparisonOperator get(String operator) {
        try {
            return new JavaLexerComparisonOperator(Type.get(operator));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public enum Type {
        EQUAL, UNEQUAL, GREATER, LESS, GREATER_EQUAL, LESS_EQUAL;

        public static Type get(String name) throws IllegalArgumentException {
            return switch (name) {
                case "==" -> EQUAL;
                case "!=" -> UNEQUAL;
                case ">" -> GREATER;
                case "<" -> LESS;
                case ">=" -> GREATER_EQUAL;
                case "<=" -> LESS_EQUAL;
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
