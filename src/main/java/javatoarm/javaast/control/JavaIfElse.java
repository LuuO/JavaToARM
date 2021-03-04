package javatoarm.javaast.control;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.expression.BooleanExpression;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.type.PrimitiveType;
import javatoarm.variable.JavaScope;
import javatoarm.variable.Variable;

/**
 * Represents if-else branch statements
 */
public class JavaIfElse implements JavaCode {
    private final JavaExpression condition;
    private final JavaCode bodyTrue, bodyFalse;
    private final String elseLabel;
    private final String endLabel;

    /**
     * Constructs an if-else statement
     *
     * @param condition the condition to evaluate
     * @param bodyTrue  code to execute if the condition evaluates to true
     * @param bodyFalse code to execute if the condition evaluates to false
     */
    public JavaIfElse(JavaExpression condition, JavaCode bodyTrue, JavaCode bodyFalse) {
        this.condition = condition;
        this.bodyTrue = bodyTrue;
        this.bodyFalse = bodyFalse;

        String uid = String.valueOf(JavaCode.getUniqueID(this));
        elseLabel = "ifelse_" + uid + "_else";
        endLabel = "ifelse_" + uid + "_end";
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        Condition opposite;
        if (condition instanceof BooleanExpression) {
            BooleanExpression comparison = (BooleanExpression) condition;
            comparison.compileToConditionCode(subroutine, parent);
            opposite = comparison.getCondition().opposite();
        } else {
            Variable conditionResult = condition.compileExpression(subroutine, parent);
            if (conditionResult.getType() != PrimitiveType.BOOLEAN) {
                throw new JTAException.InvalidOperation(condition + "does not produce a boolean value");
            }
            subroutine.checkCondition(conditionResult);
            conditionResult.deleteIfIsTemp();
            opposite = Condition.EQUAL; // Zero -> false
        }

        if (bodyFalse == null) {
            subroutine.addJump(opposite, endLabel);
            bodyTrue.compileCode(subroutine, parent);
        } else {
            subroutine.addJump(opposite, elseLabel);
            bodyTrue.compileCode(subroutine, parent);
            subroutine.addJump(Condition.ALWAYS, endLabel);
            subroutine.addLabel(elseLabel);
            bodyFalse.compileCode(subroutine, parent);
        }
        subroutine.addLabel(endLabel);
    }
}
