package javatoarm.java.type;

import javatoarm.JTAException;
import javatoarm.java.expression.JavaName;
import javatoarm.token.KeywordToken;

public class JavaSimpleType extends JavaType {
    public static JavaSimpleType STRING = new JavaSimpleType("String");
    public static JavaSimpleType NULL = new JavaSimpleType("null");
    public static JavaSimpleType BOOL = new JavaSimpleType("boolean");
    public static JavaSimpleType INT = new JavaSimpleType("int");
    public static JavaSimpleType DOUBLE = new JavaSimpleType("double");
    public static JavaSimpleType FLOAT = new JavaSimpleType("float");
    public static JavaSimpleType BYTE = new JavaSimpleType("byte");
    public static JavaSimpleType LONG = new JavaSimpleType("byte");
    public static JavaSimpleType SHORT = new JavaSimpleType("short");
    public static JavaSimpleType CHAR = new JavaSimpleType("char");
    public static JavaSimpleType VOID = new JavaSimpleType("void");

    public final String name;

    private JavaSimpleType(String name) {
        this.name = name;
    }

    public static JavaSimpleType get(JavaName name) throws JTAException {
        if (name.isSimple() && name.toSimpleName().equals("String")) {
            return STRING;
        }
        return new JavaSimpleType(name.toString());
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
            case _char -> CHAR;
            case _void -> VOID;
            default -> null;
        };
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Get the size of the type.
     *
     * @return number of bytes that one element of the type will occupy.
     */
    //TODO: improve
    public int size() {
        if (equals(INT) || equals(FLOAT)) {
            return 4;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String name() {
        return name;
    }
}
