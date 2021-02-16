package javatoarm.token.operator;

/**
 * Represents bit-level operators.
 */
public class Bitwise implements OperatorToken.Binary {
    private final Type type;

    private Bitwise(Type type) {
        this.type = type;
    }

    /**
     * Get an instances of bit-level operator
     *
     * @param operator the operator string
     * @return if the operator string is a bit-level operator, returns the corresponding operator.
     * Otherwise, returns null.
     */
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

    /**
     * Type of bit-level operation
     */
    enum Type {
        AND, OR, XOR, L_SHIFT, SIGNED_R_SHIFT, ZEROFILL_R_SHIFT;

        /**
         * Get a type with the provided op string
         *
         * @param op the op string
         * @return if the string matches one of the types, returns the type.
         * @throws IllegalArgumentException if the op string does not match any of the types
         */
        public static Type get(String op) throws IllegalArgumentException {
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
