package javatoarm.java;

import javatoarm.JTAException;

import java.util.List;

public class JavaName implements JavaRightValue, JavaLeftValue, JavaExpression {
    public final List<String> path;

    public JavaName(String name) {
        this.path = List.of(name);
    }

    public JavaName(List<String> path) {
        if (path.size() == 0)
            throw new IllegalArgumentException();
        this.path = path;
    }

    @Override
    public String toString() {
        return String.join(".", path);
    }

    public String toSimpleName() throws JTAException{
        if (path.size() != 1) {
            throw new JTAException.InvalidName(toString() + " is not a valid simple name");
        }
        return path.get(0);
    }
}
