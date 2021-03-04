package javatoarm.javaast;

import javatoarm.assembly.Subroutine;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.variable.JavaScope;
import javatoarm.variable.Variable;

import java.util.List;

/**
 * Represents a lambda expression.
 */
public class JavaLambda implements JavaExpression {
    public final List<String> arguments;
    public final JavaBlock bodyBlock;
    public final JavaExpression bodyExpression;

    /**
     * Constructs a lambda expression which has a block of codes as its body
     *
     * @param arguments the arguments
     * @param body      the body
     */
    public JavaLambda(List<String> arguments, JavaBlock body) {
        this.arguments = arguments;
        this.bodyBlock = body;
        this.bodyExpression = null;
    }

    /**
     * Constructs a lambda expression which has an expression as its body
     *
     * @param arguments the arguments
     * @param body      the body
     */
    public JavaLambda(List<String> arguments, JavaExpression body) {
        this.arguments = arguments;
        this.bodyBlock = null;
        this.bodyExpression = body;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) {
        throw new UnsupportedOperationException();
    }
}
