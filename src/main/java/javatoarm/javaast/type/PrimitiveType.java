package javatoarm.javaast.type;

import javatoarm.JTAException;
import javatoarm.token.KeywordToken;

/**
 * Represents a primitive type
 */
public enum PrimitiveType implements JavaType {
    VOID, NULL, BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE;

    /**
     * Get a primitive type with a keyword token
     *
     * @param keywordToken the keyword token
     * @return if the provided keyword token is a valid primitive type, returns the corresponding primitive type.
     * Otherwise returns null.
     */
    public static PrimitiveType get(KeywordToken keywordToken) {
        return switch (keywordToken) {
            case _boolean -> BOOLEAN;
            case _double -> DOUBLE;
            case _float -> FLOAT;
            case _byte -> BYTE;
            case _int -> INT;
            case _long -> LONG;
            case _short -> SHORT;
            case _char -> CHAR;
            case _void -> VOID;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case VOID -> "void";
            case NULL -> "null";
            case BOOLEAN -> "boolean";
            case BYTE -> "byte";
            case CHAR -> "char";
            case SHORT -> "short";
            case INT -> "int";
            case LONG -> "long";
            case FLOAT -> "float";
            case DOUBLE -> "double";
        };
    }

    @Override
    public boolean compatibleTo(JavaType that) {
        if ((that instanceof PrimitiveType)
                && (this == BYTE || this == INT || this == LONG || this == SHORT)) {

            return (that == BYTE || that == INT || that == LONG || that == SHORT);
        }
        return false;
    }

    @Override
    public int size() throws JTAException {
        if (equals(INT) || equals(FLOAT)) {
            return 4;
        }
        throw new JTAException.NotImplemented("Data type size");
    }
}
