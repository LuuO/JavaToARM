package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.OperatorToken;

//TODO bool
public class UnaryExpression implements JavaExpression {
    OperatorToken operator;
    JavaExpression operand;

    public UnaryExpression(OperatorToken token, JavaExpression operand) {
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
