package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.operator.Comparison;
import javatoarm.token.operator.Logical;
import javatoarm.token.operator.OperatorToken;
import javatoarm.variable.JavaScope;
import javatoarm.variable.TemporaryVariable;
import javatoarm.variable.Variable;

/**
 * Represents a numeric expression.
 * A numeric expression accepts two operands and gives a numeric result.
 * Examples: 1 + 1, 1 << 5
 */
public class NumericExpression implements JavaExpression {
    OperatorToken.Binary operator;
    JavaExpression operandLeft, operandRight;

    /**
     * Constructs a new NumericExpression
     *
     * @param operator     the operator
     * @param operandLeft  the left operand
     * @param operandRight the right operand
     */
    public NumericExpression(OperatorToken.Binary operator, JavaExpression operandLeft,
                             JavaExpression operandRight) {
        this.operator = operator;
        this.operandLeft = operandLeft;
        this.operandRight = operandRight;
        if (operator instanceof Comparison || operator instanceof Logical) {
            throw new IllegalArgumentException("Use BooleanExpression");
        }
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable left = operandLeft.compileExpression(subroutine, parent);
        Variable right = operandRight.compileExpression(subroutine, parent);
        JavaType resultType = left.getType();
        TemporaryVariable result = subroutine.getTemporary(resultType);
        subroutine.addALU(operator, left, right, result);
        left.deleteIfIsTemp();
        right.deleteIfIsTemp();
        return result;
    }

}
