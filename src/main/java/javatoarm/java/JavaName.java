package javatoarm.java;

public class JavaName implements JavaRightValue, JavaLeftValue, JavaExpression {
    String name;

    public JavaName(String name) {

    }

    public JavaName(JavaName arrayName, JavaExpression index) {

    }

    @Override
    public String toString() {
        return name;
    }
}
