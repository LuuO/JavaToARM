package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaLeftValue;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.variable.JavaScope;
import javatoarm.variable.Variable;

/**
 * Represents an assignment expression in Java
 */
public class JavaAssignment implements JavaExpression, JavaStatement {
    public final JavaLeftValue leftValue;
    public final JavaExpression value;

    /**
     * Constructs a new instance of JavaAssignment
     *
     * @param leftValue a left value to be assigned a new value
     * @param value     the new value
     */
    public JavaAssignment(JavaLeftValue leftValue, JavaExpression value) {
        this.leftValue = leftValue;
        this.value = value;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable left;
        if (leftValue instanceof JavaExpression) {
            left = ((JavaExpression) leftValue).compileExpression(subroutine, parent);
        } else {
            throw new JTAException.NotImplemented(leftValue.toString());
        }
        Variable right = value.compileExpression(subroutine, parent);
        subroutine.addAssignment(left, right);
        right.deleteIfIsTemp();
        return left;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable result = compileExpression(subroutine, parent);
        result.deleteIfIsTemp();
    }
}
