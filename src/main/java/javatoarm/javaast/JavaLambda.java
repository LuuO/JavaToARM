package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.Variable;

import java.util.List;

public class JavaLambda implements JavaExpression {
    public final List<String> arguments;
    public final JavaBlock bodyBlock;
    public final JavaExpression bodyExpression;

    public JavaLambda(List<String> arguments, JavaBlock body) {
        this.arguments = arguments;
        this.bodyBlock = body;
        this.bodyExpression = null;
    }

    public JavaLambda(List<String> arguments, JavaExpression body) {
        this.arguments = arguments;
        this.bodyBlock = null;
        this.bodyExpression = body;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
