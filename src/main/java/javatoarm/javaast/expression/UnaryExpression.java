package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.OperatorToken;

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
        TemporaryVariable result =
                new TemporaryVariable(parent.registerAssigner, operand.getType());
        subroutine.addALU(operator, operand, result);
        operand.deleteIfIsTemp();
        return result;
    }

}
