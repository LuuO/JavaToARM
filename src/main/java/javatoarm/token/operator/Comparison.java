package javatoarm.token.operator;

public class Comparison implements OperatorToken.Binary {
    private final Type type;

    private Comparison(Type type) {
        this.type = type;
    }

    public static Comparison get(String operator) {
        try {
            return new Comparison(Type.get(operator));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public int getPrecedenceLevel() {
        return switch (type) {
            case EQUAL, UNEQUAL -> 8;
            default -> 9;
        };
    }

    @Override
    public OperatorToken.Binary.Type getBinaryOperatorType() {
        return OperatorToken.Binary.Type.COMPARISON;
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
