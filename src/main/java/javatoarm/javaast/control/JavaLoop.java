package javatoarm.javaast.control;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.expression.ComparisonExpression;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.statement.JavaStatement;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.Variable;

public class JavaLoop implements JavaCode {
    public final String endLabel, startLabel, conditionLabel;
    JavaStatement initial;
    JavaExpression condition;
    JavaStatement increment;
    JavaCode body;
    boolean isDoWhile;

    private JavaLoop(JavaCode body, JavaStatement initial, JavaExpression condition,
                     JavaStatement increment, boolean isDoWhile) {
        this.initial = initial;
        this.condition = condition;
        this.increment = increment;
        this.isDoWhile = isDoWhile;
        this.body = body;

        endLabel = "loop_" + JavaCode.labelUID(this) + "_end";
        startLabel = "loop_" + JavaCode.labelUID(this) + "_start";
        conditionLabel = "loop_" + JavaCode.labelUID(this) + "_condition";
    }

    public static JavaLoop forLoop(JavaCode body, JavaStatement initial, JavaExpression condition,
                                   JavaStatement increment) {
        return new JavaLoop(body, initial, condition, increment, false);
    }

    public static JavaLoop whileLoop(JavaCode body, JavaExpression condition) {
        return new JavaLoop(body, null, condition, null, false);
    }

    public static JavaLoop doWhileLoop(JavaCode body, JavaExpression condition) {
        return new JavaLoop(body, null, condition, null, true);
    }

    public void addBreak(Subroutine subroutine) {
        subroutine.addJump(Condition.ALWAYS, endLabel);
    }

    public void addContinue(Subroutine subroutine) {
        subroutine.addJump(Condition.ALWAYS, conditionLabel);
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        JavaScope scope = JavaScope.newChildScope(parent, this);
        subroutine.addEmptyLine();
        subroutine.addComment("loop");

        initial.compileCode(subroutine, scope);
        if (!isDoWhile) {
            subroutine.addJump(Condition.ALWAYS, conditionLabel);
        }

        subroutine.addLabel(startLabel);
        body.compileCode(subroutine, scope);
        increment.compileCode(subroutine, scope);

        subroutine.addLabel(conditionLabel);
        Condition jumpCondition;
        if (condition instanceof ComparisonExpression) {
            ComparisonExpression comparison = (ComparisonExpression) condition;
            comparison.compileToConditionCode(subroutine, scope);
            jumpCondition = comparison.getCondition();
        } else {
            Variable condition = this.condition.compileExpression(subroutine, scope);
            subroutine.checkCondition(condition);
            jumpCondition = Condition.UNEQUAL; // Non-Zero -> true
        }
        subroutine.addJump(jumpCondition, startLabel);

        subroutine.addLabel(endLabel);
        subroutine.addEmptyLine();
    }
}
