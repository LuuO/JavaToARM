package javatoarm.java.expression;

import javatoarm.java.statement.JavaFunctionCall;
import javatoarm.java.type.JavaType;

import java.util.List;

public class NewObjectExpression extends JavaFunctionCall {
    public final JavaType dataType;

    public NewObjectExpression(JavaType dataType, List<JavaExpression> arguments) {
        super(dataType.toString(), arguments);
        this.dataType = dataType;
    }
}
