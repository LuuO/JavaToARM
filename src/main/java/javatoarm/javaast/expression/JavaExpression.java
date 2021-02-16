package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaRightValue;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Comparison;
import javatoarm.token.operator.Logical;
import javatoarm.token.operator.OperatorToken;

public interface JavaExpression extends JavaRightValue {
    static JavaExpression newBinary(OperatorToken.Binary operator,
                                    JavaExpression operandLeft, JavaExpression operandRight) {

        if (operator instanceof Comparison) {
            return new ComparisonExpression((Comparison) operator, operandLeft, operandRight);
        } else if (operator instanceof Logical) {
            return new LogicalExpression((Logical) operator, operandLeft, operandRight);
        } else {
            return new NumericExpression(operator, operandLeft, operandRight);
        }
    }
    // TODO JavaExpression analyze type

    Variable compileExpression(Subroutine subroutine, JavaScope parent)
            throws JTAException;
}
