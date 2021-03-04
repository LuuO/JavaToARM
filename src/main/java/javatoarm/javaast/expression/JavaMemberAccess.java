package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.variable.JavaScope;
import javatoarm.variable.LocalVariable;

/**
 * Represents a member access expression.
 * Examples: this.value, new String().length()
 */
public class JavaMemberAccess extends JavaMember {
    public final JavaExpression left;
    public final JavaMember right;

    /**
     * Initialize an instance of MemberAccessExpression
     *
     * @param left  expression to get member from
     * @param right member of the expression
     */
    public JavaMemberAccess(JavaExpression left, JavaMember right) {
        super(right.path);
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "%s.%s".formatted(left, right);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public LocalVariable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new JTAException.NotImplemented("MemberAccessExpression");
    }
}
