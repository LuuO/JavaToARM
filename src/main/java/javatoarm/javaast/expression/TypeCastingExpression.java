package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.type.JavaType;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.Variable;

public class TypeCastingExpression implements JavaExpression {
    public final JavaType targetType;
    public final JavaExpression expression;

    public TypeCastingExpression(JavaType castTo, JavaExpression expression) {
        this.targetType = castTo;
        this.expression = expression;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
