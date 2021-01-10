package javatoarm.lexer;

public class JavaLexerOperator extends JavaLexerToken {
    private final Type operatorType;

    protected JavaLexerOperator(Type type) {
        super(JavaLexerToken.Type.OPERATOR);
        this.operatorType = type;
    }

    public static JavaLexerToken get(String operator) {
        JavaLexerToken token = JavaLexerUnaryOperator.get(operator);
        if (token == null) {
            token = JavaLexerBinaryOperator.get(operator);
        }
        return token;
    }

    enum Type {
        // TODO: '+' is context-dependent
        UNARY, BINARY
    }
}

class JavaLexerBinaryOperator extends JavaLexerOperator {
    private final Type type;

    protected JavaLexerBinaryOperator(Type type) {
        super(JavaLexerOperator.Type.BINARY);
        this.type = type;
    }


    public static JavaLexerBinaryOperator get(String op) {
        JavaLexerBinaryOperator token;
        if ((token = JavaLexerAssignmentOperator.get(op)) != null) {
            return token;
        }
        if ((token = JavaLexerArithmeticOperator.get(op)) != null) {
            return token;
        }
        if ((token = JavaLexerComparisonOperator.get(op)) != null) {
            return token;
        }
        if ((token = JavaLexerLogicalOperator.get(op)) != null) {
            return token;
        }
        if ((token = JavaLexerBitwiseOperator.get(op)) != null) {
            return token;
        }
        return null;
    }

    enum Type {
        ASSIGNMENT, ARITHMETIC, COMPARISON, LOGICAL, BITWISE
    }
}

class JavaLexerUnaryOperator extends JavaLexerOperator {
    private final Type type;

    private JavaLexerUnaryOperator(Type type) {
        super(JavaLexerOperator.Type.UNARY);
        this.type = type;
    }

    public static JavaLexerUnaryOperator get(String operator) {
        try {
            return new JavaLexerUnaryOperator(Type.get(operator));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    enum Type {
        INCREMENT, DECREMENT, POSITIVE, NEGATIVE, LOGICAL_NOT, BITWISE_NOT;

        public static Type get(String name) throws IllegalArgumentException {
            return switch (name) {
                case "++" -> INCREMENT;
                case "--" -> DECREMENT;
                // case "+" -> POSITIVE;
                case "-" -> NEGATIVE;
                case "!" -> LOGICAL_NOT;
                case "~" -> BITWISE_NOT;
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
