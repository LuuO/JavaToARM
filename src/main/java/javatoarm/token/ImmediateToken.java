package javatoarm.token;

import javatoarm.javaast.type.JavaSimpleType;
import javatoarm.javaast.type.JavaType;

/**
 * Tokens that represent an immediate value.
 */
public interface ImmediateToken extends Token {

    /**
     * Get the corresponding immediate token
     *
     * @param s the word to analyse
     * @return if the word is a valid immediate value in Java, returns the corresponding token.
     * Otherwise returns null.
     */
    static ImmediateToken get(String s) {
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
                }
                if (s.length() == 3 && s.startsWith("'") && s.endsWith("'")) {
                    return new CharToken(s.charAt(1));
                } else {
                    return IntegerToken.get(s);
                }
        }
    }

    /**
     * Get the Java data type of the immediate value
     *
     * @return the Java data type
     */
    JavaType getType();

    Object getValue();

    /**
     * Represent an immediate string in Java
     */
    class StringToken implements ImmediateToken {
        public final String value;

        private StringToken(String value) {
            this.value = value;
        }

        /**
         * Try parsing the provided word as a String
         *
         * @param s the word
         * @return if success, returns the StringToken. Otherwise, returns null.
         */
        public static StringToken get(String s) {
            return new StringToken(s.substring(1, s.length() - 1));
        }

        @Override
        public JavaType getType() {
            return JavaSimpleType.STRING;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    /**
     * Represent a null in Java
     */
    class Null implements ImmediateToken {
        @Override
        public JavaType getType() {
            return JavaSimpleType.NULL;
        }

        @Override
        public Object getValue() {
            return null;
        }
    }

    /**
     * Represent a boolean value in Java
     */
    class Boolean implements ImmediateToken {
        boolean value;

        private Boolean(boolean value) {
            this.value = value;
        }

        @Override
        public JavaType getType() {
            return JavaSimpleType.BOOL;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    /**
     * Represent integers (int, long, short, byte) in Java.
     * // TODO: support byte
     */
    class IntegerToken implements ImmediateToken {
        public final long value;

        private IntegerToken(long value) {
            this.value = value;
        }

        /**
         * Try parsing the provided word as an integer
         *
         * @param s the word
         * @return if success, returns the IntegerToken token. Otherwise, returns null.
         */
        public static IntegerToken get(String s) {
            int radix;
            if (s.startsWith("0x")) {
                radix = 16;
                s = s.substring(2);
            } else {
                radix = 10;
            }
            if (s.endsWith("L")) {
                s = s.substring(0, s.length() - 1);
            }
            try {
                return new IntegerToken(Long.parseLong(s, radix));
            } catch (NumberFormatException nfe) {
                return null;
            }
        }

        @Override
        public JavaType getType() {
            if (value > Integer.MAX_VALUE || -value > Integer.MAX_VALUE + 1L) {
                return JavaSimpleType.LONG;
            } else if (value > Short.MAX_VALUE || -value > Short.MAX_VALUE + 1) {
                return JavaSimpleType.INT;
            } else {
                return JavaSimpleType.SHORT;
            }
        }

        @Override
        public Object getValue() {
            if (value > Integer.MAX_VALUE || -value > Integer.MAX_VALUE + 1L) {
                return value;
            } else if (value > Short.MAX_VALUE || -value > Short.MAX_VALUE + 1) {
                return (int) value;
            } else {
                return (short) value;
            }
        }
    }

    /**
     * Represent a decimal value in Java
     */
    class Decimal implements ImmediateToken {
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
            return JavaSimpleType.DOUBLE;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    class CharToken implements ImmediateToken {
        char value;

        private CharToken(char c) {
            this.value = c;
        }

        @Override
        public JavaType getType() {
            return JavaSimpleType.CHAR;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }
}
