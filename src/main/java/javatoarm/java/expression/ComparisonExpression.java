package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;
import javatoarm.java.JavaType;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Comparison;

public class ComparisonExpression implements JavaExpression {
    Comparison operator;
    JavaExpression operandLeft, operandRight;

    public ComparisonExpression(Comparison operator, JavaExpression operandLeft,
                                JavaExpression operandRight) {
        this.operator = operator;
        this.operandLeft = operandLeft;
        this.operandRight = operandRight;
    }

    public Condition getCondition() {
        return operator.condition;
    }

    public void compileToConditionCode(Subroutine parent, JavaScope scope) throws JTAException {
        Variable left = operandLeft.compileExpression(parent, scope);
        Variable right = operandRight.compileExpression(parent, scope);
        parent.addCompare(left, right);
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        compileToConditionCode(subroutine, parent);
        TemporaryVariable result = new TemporaryVariable(parent.registerAssigner, JavaType.BOOL);
        subroutine.saveBooleanResult(operator.condition, result);
        return result;
    }
}
