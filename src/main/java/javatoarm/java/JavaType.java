package javatoarm.java;

import javatoarm.token.KeywordToken;

public class JavaType {
    public static JavaType NULL = new JavaType("null", null);
    public static JavaType BOOL = new JavaType("boolean", null);
    public static JavaType INT = new JavaType("int", null);
    public static JavaType DOUBLE = new JavaType("double", null);
    public static JavaType FLOAT = new JavaType("float", null);
    public static JavaType BYTE = new JavaType("byte", null);
    public static JavaType LONG = new JavaType("byte", null);
    public static JavaType SHORT = new JavaType("short", null);

    public final String name;
    public final JavaType elementType;

    private JavaType(String name, JavaType elementType) {
        this.name = name;
        this.elementType = elementType;
    }

    public static JavaType get(String name) {
        return new JavaType(name, null);
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
            default -> null;
        };
    }

    public static JavaType getArrayTypeOf(JavaType type) {
        if (type.equals(NULL)) {
            throw new IllegalArgumentException();
        }
        return new JavaType(type.name + "[]", type);
    }

//    public boolean is(String name, boolean isArray) {
//        return this.name.equals(name) && this.isArray == isArray;
//    }

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
}
