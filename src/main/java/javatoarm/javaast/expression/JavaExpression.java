package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.JavaRightValue;
import javatoarm.token.operator.Comparison;
import javatoarm.token.operator.Logical;
import javatoarm.token.operator.OperatorToken;
import javatoarm.variable.JavaScope;
import javatoarm.variable.Variable;

/**
 * Represents a Java expression. An expression is anything that evaluates to one value.
 * <p>
 * TODO: type checking
 * </p>
 */
public interface JavaExpression extends JavaRightValue {

    /**
     * Get a binary operation expression.
     *
     * @param operator     the operator
     * @param operandLeft  left operand
     * @param operandRight right operand
     * @return the expression representing the operation
     */
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

    /**
     * Compiles this expression. If an object implements both {@link JavaExpression}
     * and {@link JavaCode}, only one of this method and
     * {@link JavaCode#compileCode(Subroutine, JavaScope)} should be called.
     *
     * @param subroutine the subroutine which this expression belongs to
     * @param parent     the parent scope of this expression
     * @return a variable storing the result of the expression
     * @throws JTAException if an error occurs
     */
    Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException;

}
