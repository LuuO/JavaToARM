package javatoarm.token;

import javatoarm.java.JavaType;

/**
 * Support only null, integers and booleans for now.
 */
public interface ValueToken extends Token {

    static ValueToken get(String s) {
        switch (s) {
            case "null":
                return new Null();
            case "true":
                return new Boolean(true);
            case "false":
                return new Boolean(false);
            default:
                if (s.startsWith("\"")) {
                    return new StringToken(s);
                } else {
                    return Integer.get(s);
                }
        }
    }

    JavaType getType();

    Object getValue();

    class StringToken implements ValueToken {
        public final String value;

        private StringToken(String value) {
            this.value = value;
        }

        public static StringToken get(String s) {
            return new StringToken(s.substring(1, s.length() - 1));
        }

        @Override
        public JavaType getType() {
            return JavaType.STRING;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

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

    class Decimal implements ValueToken {
        public final double value;

        private Decimal(double value) {
            this.value = value;
        }

        public static Decimal get(String s) {
            try {
                return new Decimal(Double.parseDouble(s));
            } catch (NumberFormatException nfe) {
                return null;
            }
        }

        @Override
        public JavaType getType() {
            return JavaType.DOUBLE;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

}
