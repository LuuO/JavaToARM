package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.JavaScope;

/**
 * Represents an expression that produces a boolean value
 */
public interface BooleanExpression extends JavaExpression {

    /**
     * Compiles this expression and the result is reflected in condition codes.
     *
     * @param subroutine the subroutine which this expression belongs to
     * @param parent     the parent scope of this expression
     * @throws JTAException if an error occurs
     */
    void compileToConditionCode(Subroutine subroutine, JavaScope parent) throws JTAException;

    /**
     * The condition required for this expression to evaluate to true.
     *
     * @return the condition required for a "true" result
     */
    Condition getCondition() throws JTAException;
}
