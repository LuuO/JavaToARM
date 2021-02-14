package javatoarm.java.type;

import javatoarm.java.expression.JavaName;

public class JavaParametrizedType extends JavaType{
    public final JavaName name;
    public final JavaType parameter;

    public JavaParametrizedType(JavaName name, JavaType parameter) {
        this.name = name;
        this.parameter = parameter;
    }

    @Override
    public String name() {
        return "%s<%s>".formatted(name, parameter);
    }
}
