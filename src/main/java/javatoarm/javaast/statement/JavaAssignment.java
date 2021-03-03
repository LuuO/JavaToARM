package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaLeftValue;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.Variable;

/**
 * Represents an assignment expression in Java
 */
public class JavaAssignment implements JavaExpression, JavaStatement {
    public final JavaLeftValue leftValue;
    public final JavaExpression value;

    public JavaAssignment(JavaLeftValue leftValue, JavaExpression value) {
        this.leftValue = leftValue;
        this.value = value;
    }

    /**
     * Only one of compileExpression and compileStatement will be called.
     *
     * @param subroutine
     * @param parent
     * @return
     * @throws JTAException
     */
    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable left;
        if (leftValue instanceof JavaExpression) {
            left = ((JavaExpression) leftValue).compileExpression(subroutine, parent);
        } else {
            throw new UnsupportedOperationException();
        }
        Variable right = value.compileExpression(subroutine, parent);
        subroutine.addAssignment(left, right);
        return left;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable result = compileExpression(subroutine, parent);
        result.deleteIfIsTemp();
    }
}
