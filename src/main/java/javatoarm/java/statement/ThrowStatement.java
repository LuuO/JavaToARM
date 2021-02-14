package javatoarm.java.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;
import javatoarm.java.expression.NewObjectExpression;

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
