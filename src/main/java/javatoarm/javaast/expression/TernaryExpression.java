package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.Variable;

/**
 * Represents a ternary expression.
 * Examples: true ? 1 : 0
 */
public class TernaryExpression implements JavaExpression {
    public final JavaExpression condition;
    public final JavaExpression trueExpression;
    public final JavaExpression falseExpression;

    /**
     * Constructs a new TernaryExpression
     *
     * @param condition       condition
     * @param trueExpression  expression to evaluate if the condition is true
     * @param falseExpression expression to evaluate if the condition is false
     */
    public TernaryExpression(JavaExpression condition,
                             JavaExpression trueExpression, JavaExpression falseExpression) {
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new JTAException.NotImplemented("TernaryExpression");
    }
}
