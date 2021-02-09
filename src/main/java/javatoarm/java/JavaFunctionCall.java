package javatoarm.java;

import java.util.List;

public class JavaFunctionCall implements JavaExpression, JavaStatement {
    JavaType dataType;
    String name;
    List<JavaExpression> arguments;

    public JavaFunctionCall(String name, List<JavaExpression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public JavaFunctionCall(JavaType dataType, List<JavaExpression> arguments) {
        this.dataType = dataType;
        this.arguments = arguments;
    }
}
