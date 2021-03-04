package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.expression.NewObjectExpression;
import javatoarm.variable.JavaScope;

/**
 * A throw statement.
 * Example: throw new UnsupportedOperationException();
 */
public class ThrowStatement implements JavaStatement {
    public final NewObjectExpression toThrow;

    /**
     * Constructs an instance of ThrowStatement
     *
     * @param toThrow the expression to throw
     */
    public ThrowStatement(NewObjectExpression toThrow) {
        this.toThrow = toThrow;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new JTAException.NotImplemented(toString());
    }
}
