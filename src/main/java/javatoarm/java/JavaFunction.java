package javatoarm.java;

import java.util.List;
import java.util.Set;

public class JavaFunction implements JavaClass.Member {
    private final JavaType returnType;
    private final String name;
    private final JavaBlock body;

    public JavaFunction(Set<JavaProperty> properties, JavaType returnType,
                        String name, List<JavaVariableDeclare> arguments, JavaBlock body) {
        this.returnType = returnType;
        this.name = name;
        this.body = body;
    }

    @Override
    public JavaType type() {
        return returnType;
    }

    @Override
    public String name() {
        return name;
    }
}
