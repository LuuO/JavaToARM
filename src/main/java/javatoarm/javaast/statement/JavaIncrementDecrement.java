package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaMember;
import javatoarm.variable.JavaScope;
import javatoarm.variable.TemporaryVariable;
import javatoarm.variable.Variable;

/**
 * Represents an increment or decrement expression/statement.
 * Examples: i++, i--
 */
public class JavaIncrementDecrement implements JavaExpression, JavaStatement {
    public final JavaMember member;
    public final boolean post;
    public final boolean increase;

    /**
     * Constructs a new increment or decrement expression/statement.
     *
     * @param member   member to change value
     * @param post     true if the increment or decrement happens after using the value, false otherwise
     * @param increase true for an increment, false for a decrement
     */
    public JavaIncrementDecrement(JavaMember member, boolean post, boolean increase) {
        this.member = member;
        this.post = post;
        this.increase = increase;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable variable = this.member.compileExpression(subroutine, parent);
        if (post) {
            TemporaryVariable temp = subroutine.getTemporary(variable.getType());
            subroutine.addAssignment(temp, variable);
            subroutine.addIncrementDecrement(variable, increase);
            variable.deleteIfIsTemp();
            return temp;
        } else {
            subroutine.addIncrementDecrement(variable, increase);
            return variable;
        }
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable variable = this.member.compileExpression(subroutine, parent);
        subroutine.addIncrementDecrement(variable, increase);
        variable.deleteIfIsTemp();
    }
}
