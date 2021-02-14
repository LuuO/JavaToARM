package javatoarm.token.operator;

public class Bitwise implements OperatorToken.Binary {
    private final Type type;

    private Bitwise(Type type) {
        this.type = type;
    }

    public static Bitwise get(String operator) {
        try {
            return new Bitwise(Type.get(operator));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public int getPrecedenceLevel() {
        return switch (type) {
            case AND -> 7;
            case XOR -> 6;
            case OR -> 5;
            default -> 10;
        };
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
