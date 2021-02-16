package javatoarm.javaast.type;

import javatoarm.javaast.expression.JavaName;

import java.util.List;
import java.util.stream.Collectors;

public class JavaParametrizedType extends JavaType {
    public final JavaName name;
    public final List<JavaType> parameters;

    public JavaParametrizedType(JavaName name, List<JavaType> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String name() {
        return "%s<%s>".formatted(name,
                parameters.stream().map(JavaType::name).collect(Collectors.joining(", ")));
    }
}
