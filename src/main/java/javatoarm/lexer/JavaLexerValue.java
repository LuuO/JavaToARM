package javatoarm.lexer;

/**
 * Support only null, integers and booleans for now.
 */
public class JavaLexerValue extends JavaLexerToken {
    private final Type valueType;

    protected JavaLexerValue(Type type) {
        super(JavaLexerToken.Type.VALUE);
        this.valueType = type;
    }

    public static JavaLexerValue get(String s) {
        if (s.equals("null")) {
            return new JavaLexerNull();
        }
        JavaLexerValue value;
        if ((value = JavaLexerBoolean.get(s)) != null) {
            return value;
        }
        if ((value = JavaLexerInteger.get(s)) != null) {
            return value;
        }
        return null;
    }

    enum Type {
        NULL, BOOLEAN, INTEGER
    }
}

class JavaLexerNull extends JavaLexerValue {
    public JavaLexerNull() {
        super(Type.NULL);
    }
}

class JavaLexerBoolean extends JavaLexerValue {
    boolean value;

    private JavaLexerBoolean(boolean value) {
        super(Type.BOOLEAN);
        this.value = value;
    }

    public static JavaLexerBoolean get(String s) {
        return switch (s) {
            case "true" -> new JavaLexerBoolean(true);
            case "false" -> new JavaLexerBoolean(false);
            default -> null;
        };
    }
}

class JavaLexerInteger extends JavaLexerValue {
    int value;

    private JavaLexerInteger(int value) {
        super(Type.INTEGER);
        this.value = value;
    }

    public static JavaLexerInteger get(String s) {
        try {
            return new JavaLexerInteger(Integer.parseInt(s));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}
