package javatoarm.java;

public class JavaName implements JavaRightValue, JavaLeftValue, JavaExpression {
    public final String name;

    public JavaName(String name) {
        this.name = name;
    }

    //TODO: create a new data type for this
    public JavaName(JavaName arrayName, JavaExpression index) {
        this.name = arrayName.toString();
    }

    @Override
    public String toString() {
        return name;
    }
}
