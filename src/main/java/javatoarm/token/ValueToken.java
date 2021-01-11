package javatoarm.token;

/**
 * Support only null, integers and booleans for now.
 */
public interface ValueToken extends Token {

    static ValueToken get(String s) {
        return switch (s) {
            case "null" -> new Null();
            case "true" -> new Boolean(true);
            case "false" -> new Boolean(false);
            default -> Integer.get(s);
        };
    }

    @Override
    default Token.Type getTokenType() {
        return Token.Type.VALUE;
    }

    Type getValueType();

    enum Type {
        NULL, BOOLEAN, INTEGER
    }

    class Null implements ValueToken {
        @Override
        public ValueToken.Type getValueType() {
            return ValueToken.Type.NULL;
        }
    }

    class Boolean implements ValueToken {
        boolean value;

        private Boolean(boolean value) {
            this.value = value;
        }

        @Override
        public ValueToken.Type getValueType() {
            return ValueToken.Type.BOOLEAN;
        }
    }

    class Integer implements ValueToken {
        int value;

        private Integer(int value) {
            this.value = value;
        }

        public static Integer get(String s) {
            try {
                return new Integer(java.lang.Integer.parseInt(s));
            } catch (NumberFormatException nfe) {
                return null;
            }
        }

        @Override
        public ValueToken.Type getValueType() {
            return ValueToken.Type.INTEGER;
        }
    }

}
