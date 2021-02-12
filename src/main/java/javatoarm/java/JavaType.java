package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.java.expression.JavaName;
import javatoarm.token.KeywordToken;

public class JavaType {
    public static JavaType STRING = new JavaType("String", null);
    public static JavaType NULL = new JavaType("null", null);
    public static JavaType BOOL = new JavaType("boolean", null);
    public static JavaType INT = new JavaType("int", null);
    public static JavaType DOUBLE = new JavaType("double", null);
    public static JavaType FLOAT = new JavaType("float", null);
    public static JavaType BYTE = new JavaType("byte", null);
    public static JavaType LONG = new JavaType("byte", null);
    public static JavaType SHORT = new JavaType("short", null);
    public static JavaType VOID = new JavaType("void", null);

    public final String name;
    public final JavaType elementType;

    private JavaType(String name, JavaType elementType) {
        this.name = name;
        this.elementType = elementType;
    }

    public static JavaType get(JavaName name) throws JTAException {
        if (name.toSimpleName().equals("String")) {
            return STRING;
        }
        return new JavaType(name.toString(), null);
    }

    public static JavaType get(KeywordToken keywordToken) {
        return switch (keywordToken.keyword) {
            case _boolean -> BOOL;
            case _double -> DOUBLE;
            case _float -> FLOAT;
            case _byte -> BYTE;
            case _int -> INT;
            case _long -> LONG;
            case _short -> SHORT;
            case _void -> VOID;
            default -> null;
        };
    }

    public static JavaType getArrayTypeOf(JavaType type) {
        if (type.equals(NULL)) {
            throw new IllegalArgumentException();
        }
        return new JavaType(type.name + "[]", type);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavaType) {
            JavaType that = (JavaType) obj;
            return this.name.equals(that.name);
        }
        return false;
    }

    /**
     * Get the size of the type.
     * TODO: improve
     *
     * @return number of bytes that one element of the type will occupy.
     */
    public int size() {
        if (equals(INT) || equals(FLOAT)) {
            return 4;
        }
        throw new UnsupportedOperationException();
    }
}
