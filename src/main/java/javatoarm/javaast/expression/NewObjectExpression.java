package javatoarm.javaast.expression;

import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.statement.JavaFunctionCall;
import javatoarm.javaast.type.JavaType;

import java.util.List;

/**
 * Represents an object creation expression.
 * Examples: new Object(), new String()
 */
public class NewObjectExpression extends JavaFunctionCall {
    public final JavaType dataType;

    /**
     * Constructs an instance of NewObjectExpression
     *
     * @param dataType  the data type to create
     * @param arguments constructor arguments
     */
    public NewObjectExpression(JavaType dataType, List<JavaRightValue> arguments) {
        super(dataType.toString(), arguments);
        this.dataType = dataType;
    }
}
