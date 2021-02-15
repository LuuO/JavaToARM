package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;
import javatoarm.staticanalysis.LocalVariable;

public class MemberAccessExpression extends JavaName {
    public final JavaExpression left;
    public final JavaName right;

    public MemberAccessExpression(JavaExpression left, JavaName right) {
        super(right.path);
        this.left = left;
        this.right = right;
    }

    /**
     * TODO: implement
     *
     * @return a String representation of the expression
     */
    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public LocalVariable compileExpression(Subroutine subroutine, JavaScope parent)
        throws JTAException {
        throw new UnsupportedOperationException();
    }
}
