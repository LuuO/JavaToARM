package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.type.JavaType;
import javatoarm.variable.JavaScope;
import javatoarm.variable.Variable;

/**
 * Represents an type casting expression.
 * Example: (List) object
 */
public class TypeCastingExpression implements JavaExpression {
    public final JavaType targetType;
    public final JavaExpression expression;

    /**
     * Constructs a new TypeCastingExpression
     *
     * @param castTo     target type
     * @param expression the expression to be casted
     */
    public TypeCastingExpression(JavaType castTo, JavaExpression expression) {
        this.targetType = castTo;
        this.expression = expression;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) {
        throw new JTAException.NotImplemented("TypeCastingExpression");
    }
}
