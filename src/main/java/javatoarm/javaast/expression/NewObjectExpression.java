package javatoarm.javaast.expression;

import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.statement.JavaFunctionCall;
import javatoarm.javaast.type.JavaType;

import java.util.List;

public class NewObjectExpression extends JavaFunctionCall {
    public final JavaType dataType;

    public NewObjectExpression(JavaType dataType, List<JavaRightValue> arguments) {
        super(dataType.toString(), arguments);
        this.dataType = dataType;
    }
}
