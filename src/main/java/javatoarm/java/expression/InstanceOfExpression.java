package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;
import javatoarm.java.type.JavaType;
import javatoarm.staticanalysis.Variable;

public class InstanceOfExpression implements BooleanExpression {

    public final JavaExpression left;
    public final JavaType type;

    public InstanceOfExpression(JavaExpression left, JavaType type) {
        this.left = left;
        this.type = type;
    }

    @Override
    public void compileToConditionCode(Subroutine parent, JavaScope scope) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Condition getCondition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
