package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.LocalVariable;

public class MemberAccessExpression extends JavaMember {
    public final JavaExpression left;
    public final JavaMember right;

    public MemberAccessExpression(JavaExpression left, JavaMember right) {
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
        return left.toString() + right.toString();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public LocalVariable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
