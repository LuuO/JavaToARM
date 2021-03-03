package javatoarm.token;

import javatoarm.JTAException;
import javatoarm.javaast.type.JavaType;
import javatoarm.javaast.type.PrimitiveType;
import javatoarm.javaast.type.UserDefinedType;

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

    /**
     * Get the immediate value with corresponding type
     *
     * @return the immediate value of type {@link ImmediateToken#getType()}
     * @throws JTAException if an error occurs
     */
    Object getValue() throws JTAException;

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
            return UserDefinedType.STRING;
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
            return PrimitiveType.NULL;
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
            return PrimitiveType.BOOLEAN;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    /**
     * Represent integers (int, long, short, byte) in Java.
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
                return PrimitiveType.LONG;
            } else if (value > Short.MAX_VALUE || -value > Short.MAX_VALUE + 1) {
                return PrimitiveType.INT;
            } else if (value > Byte.MAX_VALUE || -value > Byte.MAX_VALUE + 1) {
                return PrimitiveType.SHORT;
            } else {
                return PrimitiveType.BYTE;
            }
        }

        @Override
        public Object getValue() throws JTAException {
            JavaType type = getType();
            if (PrimitiveType.LONG.equals(type)) {
                return value;
            } else if (PrimitiveType.INT.equals(type)) {
                return (int) value;
            } else if (PrimitiveType.SHORT.equals(type)) {
                return (short) value;
            } else if (PrimitiveType.BYTE.equals(type)) {
                return (byte) value;
            }
            throw new JTAException.NotImplemented(getType().toString());
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
            return PrimitiveType.DOUBLE;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    /**
     * Represent a char value in Java
     */
    class CharToken implements ImmediateToken {
        char value;

        private CharToken(char c) {
            this.value = c;
        }

        @Override
        public JavaType getType() {
            return PrimitiveType.CHAR;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }
}
