package javatoarm.java;

import javatoarm.token.ValueToken;

public class JavaImmediate implements JavaRightValue, JavaExpression {
    public final JavaType type;
    public final Object value;

    public JavaImmediate(JavaType type, Object value) {
        // TODO: check parameters
        this.type = type;
        this.value = value;
    }

    public JavaImmediate(ValueToken token) {
        this(token.getType(), token.getValue());
    }
}
