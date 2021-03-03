package javatoarm.javaast.control;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.expression.BooleanExpression;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.Variable;

public class JavaIfElse implements JavaCode {
    JavaExpression condition;
    JavaCode bodyTrue, bodyFalse;
    String elseLabel = "ifelse_" + JavaCode.getUniqueID(this) + "_else";
    String endLabel = "ifelse_" + JavaCode.getUniqueID(this) + "_end";

    public JavaIfElse(JavaExpression condition, JavaCode bodyTrue, JavaCode bodyFalse) {
        this.condition = condition;
        this.bodyTrue = bodyTrue;
        this.bodyFalse = bodyFalse;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        Condition opposite;
        if (condition instanceof BooleanExpression) {
            BooleanExpression comparison = (BooleanExpression) condition;
            comparison.compileToConditionCode(subroutine, parent);
            opposite = comparison.getCondition().opposite();
        } else {
            Variable condition = this.condition.compileExpression(subroutine, parent);
            subroutine.checkCondition(condition);
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
