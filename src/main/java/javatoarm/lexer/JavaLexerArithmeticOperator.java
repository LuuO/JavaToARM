package javatoarm.lexer;

public class JavaLexerArithmeticOperator extends JavaLexerBinaryOperator {
    private final Type arithmeticOperatorType;

    private JavaLexerArithmeticOperator(Type type) {
        super(JavaLexerBinaryOperator.Type.ARITHMETIC);
        this.arithmeticOperatorType = type;
    }

    public static JavaLexerArithmeticOperator get(String operator) {
        if (operator.length() != 1) {
            return null;
        }
        try {
            return new JavaLexerArithmeticOperator(Type.get(operator.charAt(0)));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    enum Type {
        ADD, SUB, MUL, DIV, MOD;

        public static Type get(char c) {
            return switch (c) {
                case '+' -> ADD;
                case '-' -> SUB;
                case '*' -> MUL;
                case '/' -> DIV;
                case '%' -> MOD;
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
