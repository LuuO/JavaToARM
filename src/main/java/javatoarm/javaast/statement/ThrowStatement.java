package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.expression.NewObjectExpression;
import javatoarm.staticanalysis.JavaScope;

public class ThrowStatement implements JavaStatement {
    public final NewObjectExpression toThrow;

    public ThrowStatement(NewObjectExpression toThrow) {
        this.toThrow = toThrow;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
