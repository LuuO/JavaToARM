package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.ImmediateToken;
import javatoarm.variable.Immediate;
import javatoarm.variable.JavaScope;
import javatoarm.variable.Variable;

/**
 * Represents an immediate value in Java.
 * <p>
 * Examples: 1, true, 3.0
 * </p>
 */
public class ImmediateExpression implements JavaRightValue, JavaExpression {
    public final JavaType type;
    public final Object value;

    /**
     * Constructs a new ImmediateExpression
     *
     * @param type  type of the immediate value
     * @param value the immediate value
     */
    public ImmediateExpression(JavaType type, Object value) {
        // TODO: check if value matches type
        this.type = type;
        this.value = value;
    }

    /**
     * Constructs a new ImmediateExpression
     *
     * @param token token representing the immediate value
     */
    public ImmediateExpression(ImmediateToken token) throws JTAException {
        this(token.getType(), token.getValue());
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        return new Immediate(type, value);
    }

}
