package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;
import javatoarm.java.type.JavaSimpleType;
import javatoarm.java.type.JavaType;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Comparison;

public class ComparisonExpression implements BooleanExpression {
    Condition condition;
    JavaExpression operandLeft, operandRight;

    public ComparisonExpression(Comparison operator, JavaExpression operandLeft,
                                JavaExpression operandRight) {
        this.condition = operator.getCondition();
        this.operandLeft = operandLeft;
        this.operandRight = operandRight;
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public void compileToConditionCode(Subroutine parent, JavaScope scope) throws JTAException {
        Variable left = operandLeft.compileExpression(parent, scope);
        Variable right = operandRight.compileExpression(parent, scope);
        parent.addCompare(left, right);
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        compileToConditionCode(subroutine, parent);
        TemporaryVariable result = new TemporaryVariable(parent.registerAssigner, JavaSimpleType.BOOL);
        subroutine.saveBooleanResult(condition, result);
        return result;
    }
}
