package javatoarm.token;

import javatoarm.java.JavaType;

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

    JavaType getType();
    Object getValue();

    class Null implements ValueToken {
        @Override
        public JavaType getType() {
            return JavaType.NULL;
        }

        @Override
        public Object getValue() {
            return null;
        }
    }

    class Boolean implements ValueToken {
        boolean value;

        private Boolean(boolean value) {
            this.value = value;
        }

        @Override
        public JavaType getType() {
            return JavaType.BOOL;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    class Integer implements ValueToken {
        public final int value;

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
        public JavaType getType() {
            return JavaType.INT;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

}
