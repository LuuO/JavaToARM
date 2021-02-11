package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;
import javatoarm.java.JavaType;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Comparison;
import javatoarm.token.operator.OperatorToken;

public class NumericExpression implements JavaExpression {
    OperatorToken.Binary operator;
    JavaExpression operandLeft, operandRight;

    public NumericExpression(OperatorToken.Binary operator, JavaExpression operandLeft,
                             JavaExpression operandRight) {
        this.operator = operator;
        this.operandLeft = operandLeft;
        this.operandRight = operandRight;
        if (operator instanceof Comparison) {
            throw new IllegalArgumentException("Use ComparisonExpression");
        }
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable left = operandLeft.compileExpression(subroutine, parent);
        Variable right = operandRight.compileExpression(subroutine, parent);
        JavaType resultType = left.getType();
        TemporaryVariable result = new TemporaryVariable(parent.registerAssigner, resultType);
        subroutine.addALU(operator, left, right, result);
        return result;
    }
}
