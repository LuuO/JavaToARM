package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.type.PrimitiveType;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Logical;

/**
 * Represents a logical expression.
 * Examples: true && false, false || true
 */
public class LogicalExpression implements BooleanExpression {
    Logical operator;
    JavaExpression operandLeft, operandRight;

    /**
     * Constructs an instance of logical expression
     *
     * @param operator     the logical operator
     * @param operandLeft  left operand
     * @param operandRight right operand
     */
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
    public void compileToConditionCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        compile(subroutine, parent, false);
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        return compile(subroutine, parent, true);
    }

    /**
     * Compiles this logical expression. Result of the operation can be saved
     * to a temporary variable or to the condition code. Note that when
     * saveResult is true, the effect on the condition code is undetermined.
     *
     * @param subroutine the subroutine which this expression belongs to
     * @param parent     the parent scope of this expression
     * @param saveResult true to save the result to a variable and return the variable, false to
     *                   modify only the condition code.
     * @return if saveResult is true, returns the temporary variable that stores the result.
     * Otherwise, the behavior of the returned variable is undefined.
     * @throws JTAException if an error occurs
     */
    private Variable compile(Subroutine subroutine, JavaScope parent, boolean saveResult)
            throws JTAException {
        Variable left = operandLeft.compileExpression(subroutine, parent);
        Variable right = operandRight.compileExpression(subroutine, parent);
        TemporaryVariable result = subroutine.getTemporary(PrimitiveType.BOOLEAN);
        subroutine.addLogicalOperation(saveResult, operator == Logical.AND, left, right, result);
        left.deleteIfIsTemp();
        right.deleteIfIsTemp();
        return result;
    }
}
