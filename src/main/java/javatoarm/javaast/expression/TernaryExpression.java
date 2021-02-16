package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaScope;
import javatoarm.staticanalysis.Variable;

public class TernaryExpression implements JavaExpression {
    public final JavaExpression condition;
    public final JavaExpression trueExpression;
    public final JavaExpression falseExpression;

    public TernaryExpression(JavaExpression condition,
                             JavaExpression trueExpression, JavaExpression falseExpression) {
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
