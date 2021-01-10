package javatoarm.lexer;

public class JavaLexerBitwiseOperator extends JavaLexerBinaryOperator {
    private final Type bitwiseOperatorType;

    private JavaLexerBitwiseOperator(Type type) {
        super(JavaLexerBinaryOperator.Type.BITWISE);
        this.bitwiseOperatorType = type;
    }

    public static JavaLexerBitwiseOperator get(String operator) {
        try {
            return new JavaLexerBitwiseOperator(Type.get(operator));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    enum Type {
        AND, OR, XOR, L_SHIFT, SIGNED_R_SHIFT, ZEROFILL_R_SHIFT;

        public static Type get(String op) {
            return switch (op) {
                case "&" -> AND;
                case "|" -> OR;
                case "^" -> XOR;
                case "<<" -> L_SHIFT;
                case ">>" -> SIGNED_R_SHIFT;
                case ">>>" -> ZEROFILL_R_SHIFT;

                default -> throw new IllegalArgumentException();
            };
        }
    }
}
