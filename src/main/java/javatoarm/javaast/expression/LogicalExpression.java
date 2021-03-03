package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.type.PrimitiveType;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Logical;

public class LogicalExpression implements BooleanExpression {
    Logical operator;
    JavaExpression operandLeft, operandRight;

    public LogicalExpression(Logical operator, JavaExpression operandLeft,
                             JavaExpression operandRight) {
        this.operator = operator;
        this.operandLeft = operandLeft;
        this.operandRight = operandRight;
    }

    @Override
    public Condition getCondition() {
        return Condition.UNEQUAL;
    }

    @Override
    public void compileToConditionCode(Subroutine subroutine, JavaScope parent)
            throws JTAException {

        compile(subroutine, parent, false);
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        return compile(subroutine, parent, true);
    }

    private Variable compile(Subroutine subroutine, JavaScope parent, boolean saveResult)
            throws JTAException {

        Variable left = operandLeft.compileExpression(subroutine, parent);
        Variable right = operandRight.compileExpression(subroutine, parent);
        TemporaryVariable result = new TemporaryVariable(parent.registerAssigner, PrimitiveType.BOOLEAN);
        subroutine.addLogic(saveResult, operator == Logical.AND, left, right, result);
        return result;
    }
}
