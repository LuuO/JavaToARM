package javatoarm.token;

import javatoarm.java.type.JavaSimpleType;
import javatoarm.java.type.JavaType;

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
                } else {
                    return Int.get(s);
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
     * Represent an integer in Java
     */
    class Int implements ImmediateToken {
        public final int value;

        private Int(int value) {
            this.value = value;
        }

        /**
         * Try parsing the provided word as an integer
         *
         * @param s the word
         * @return if success, returns the Int token. Otherwise, returns null.
         */
        public static Int get(String s) {
            try {
                if (s.startsWith("0x")) {
                    return new Int(Integer.parseInt(s.substring(2), 16));
                } else {
                    return new Int(Integer.parseInt(s));
                }
            } catch (NumberFormatException nfe) {
                return null;
            }
        }

        @Override
        public JavaType getType() {
            return JavaSimpleType.INT;
        }

        @Override
        public Object getValue() {
            return value;
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

}
