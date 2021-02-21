package javatoarm.javaast.type;

import javatoarm.JTAException;
import javatoarm.javaast.expression.JavaMember;
import javatoarm.token.KeywordToken;

// TODO enum?
// user defined type?
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

    public static JavaSimpleType get(JavaMember typePath) throws JTAException {
        if (typePath.isSimple() && typePath.toSimpleName().equals("String")) {
            return STRING;
        }
        return new JavaSimpleType(typePath.toString());
    }

    public static JavaType get(KeywordToken keywordToken) {
        return switch (keywordToken) {
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
    public boolean compatibleTo(Object obj) {
        if (super.compatibleTo(obj)) {
            return true;
        }
        if ((obj instanceof JavaSimpleType)
                && (this == BYTE || this == INT || this == LONG || this == SHORT)) {

            return (obj == BYTE || obj == INT || obj == LONG || obj == SHORT);
        }
        return false;
    }

    /**
     * Get the size of the type.
     * TODO: support other types
     *
     * @return number of bytes that one element of the type will occupy.
     */
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
