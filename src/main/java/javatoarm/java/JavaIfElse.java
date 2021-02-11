package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.java.expression.ComparisonExpression;
import javatoarm.java.expression.JavaExpression;
import javatoarm.staticanalysis.Variable;

public class JavaIfElse implements JavaCode {
    JavaExpression condition;
    JavaCode bodyTrue, bodyFalse;
    String elseLabel = "ifelse_" + JavaCode.labelUID(this) + "_else";
    String endLabel = "ifelse_" + JavaCode.labelUID(this) + "_end";

    public JavaIfElse(JavaExpression condition, JavaCode bodyTrue, JavaCode bodyFalse) {
        this.condition = condition;
        this.bodyTrue = bodyTrue;
        this.bodyFalse = bodyFalse;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        Condition opposite;
        if (condition instanceof ComparisonExpression) {
            ComparisonExpression comparison = (ComparisonExpression) condition;
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
