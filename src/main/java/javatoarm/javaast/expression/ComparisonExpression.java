package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.type.PrimitiveType;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Comparison;

/**
 * Represents a comparison expression in Java.
 * <p>
 * Examples: 1 == 2, 2 &lt 4
 * </p>
 */
public class ComparisonExpression implements BooleanExpression {
    private final Condition condition;
    private final JavaExpression operandLeft, operandRight;

    /**
     * Constructs a new ComparisonExpression
     *
     * @param operator     the operator
     * @param operandLeft  left operand
     * @param operandRight right operand
     */
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
    public void compileToConditionCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable left = operandLeft.compileExpression(subroutine, parent);
        Variable right = operandRight.compileExpression(subroutine, parent);
        subroutine.addCompare(left, right);
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        compileToConditionCode(subroutine, parent);
        TemporaryVariable result = new TemporaryVariable(parent.registerAssigner, PrimitiveType.BOOLEAN);
        subroutine.saveBooleanResult(condition, result);
        return result;
    }
}
