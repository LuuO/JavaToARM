package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.token.operator.OperatorToken;
import javatoarm.variable.JavaScope;
import javatoarm.variable.TemporaryVariable;
import javatoarm.variable.Variable;

/**
 * Represents an unary expression.
 * Examples: -i, !true
 */
public class UnaryExpression implements JavaExpression {
    OperatorToken.Unary operator;
    JavaExpression operand;

    /**
     * Constructs a new UnaryExpression
     *
     * @param token   the unary operator token
     * @param operand the operand
     */
    public UnaryExpression(OperatorToken.Unary token, JavaExpression operand) {
        this.operator = token;
        this.operand = operand;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable operand = this.operand.compileExpression(subroutine, parent);
        TemporaryVariable result = subroutine.getTemporary(operand.getType());
        subroutine.addALU(operator, operand, result);
        operand.deleteIfIsTemp();
        return result;
    }

}
