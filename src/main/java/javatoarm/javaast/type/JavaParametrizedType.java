package javatoarm.javaast.type;

import javatoarm.javaast.expression.JavaMember;

import java.util.List;
import java.util.stream.Collectors;

public class JavaParametrizedType extends JavaType {
    public final JavaMember typePath;
    public final List<JavaType> parameters;

    public JavaParametrizedType(JavaMember typePath, List<JavaType> parameters) {
        this.typePath = typePath;
        this.parameters = parameters;
    }

    @Override
    public String name() {
        return "%s<%s>".formatted(typePath,
                parameters.stream().map(JavaType::name).collect(Collectors.joining(", ")));
    }
}
