package javatoarm.java;

import javatoarm.JTAException;

import java.util.List;
import java.util.Set;

public class JavaVariableDeclare implements JavaClass.Member, JavaStatement {
    private final JavaType type;
    private final String name;
    private final JavaRightValue initialValue;
    private final Set<JavaProperty> properties;

    public JavaVariableDeclare(Set<JavaProperty> properties, JavaType type,
                               String name, JavaRightValue initialValue) throws JTAException {
        this.type = type;
        this.name = name;
        this.properties = properties; // TODO: validate properties
        this.initialValue = initialValue;
    }

    public boolean hasInitialValue() {
        return initialValue != null;
    }

    public boolean hasProperties() {
        return properties.size() != 0;
    }

    @Override
    public JavaType type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }
}
