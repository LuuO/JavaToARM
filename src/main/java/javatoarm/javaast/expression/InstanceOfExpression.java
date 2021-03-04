package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.type.JavaType;
import javatoarm.variable.JavaScope;
import javatoarm.variable.Variable;

/**
 * Represents an instanceof expression.
 * An instanceof expression checks the type of some value.
 * <p>
 * Examples: "1" instanceof String, e instanceof Expression
 * </p>
 */
public class InstanceOfExpression implements BooleanExpression {
    public final JavaExpression left;
    public final JavaType type;

    /**
     * Constructs a new InstanceOfExpression
     *
     * @param left the value to check
     * @param type the type to check against with
     */
    public InstanceOfExpression(JavaExpression left, JavaType type) {
        this.left = left;
        this.type = type;
    }

    @Override
    public void compileToConditionCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new JTAException.NotImplemented("InstanceOfExpression");
    }

    @Override
    public Condition getCondition() throws JTAException {
        throw new JTAException.NotImplemented("InstanceOfExpression");
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new JTAException.NotImplemented("InstanceOfExpression");
    }
}
