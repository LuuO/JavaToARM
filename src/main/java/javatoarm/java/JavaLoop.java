package javatoarm.java;

import javatoarm.Condition;
import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.expression.ComparisonExpression;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.statement.JavaStatement;
import javatoarm.staticanalysis.Variable;

public class JavaLoop implements JavaCode {
    JavaStatement initial;
    JavaExpression condition;
    JavaStatement increment;
    JavaCode body;
    boolean isDoWhile;

    public final String endLabel, startLabel, conditionLabel;

    private JavaLoop(JavaCode body, JavaStatement initial, JavaExpression condition,
                     JavaStatement increment, boolean isDoWhile) {
        this.initial = initial;
        this.condition = condition;
        this.increment = increment;
        this.isDoWhile = isDoWhile;
        this.body = body;

        endLabel = "loop_" + toString() + "_end";
        startLabel = "loop_" + toString() + "_start";
        conditionLabel = "loop_" + toString() + "_condition";
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

        initial.compileCode(subroutine, scope);

        subroutine.addLabel(startLabel);
        body.compileCode(subroutine, scope);
        increment.compileCode(subroutine, scope);

        subroutine.addLabel(conditionLabel);
        Condition opposite;
        if (condition instanceof ComparisonExpression) {
            ComparisonExpression comparison = (ComparisonExpression) condition;
            comparison.compileToConditionCode(subroutine, scope);
            opposite = comparison.getCondition().opposite();
        } else {
            Variable condition = this.condition.compileExpression(subroutine, scope);
            subroutine.checkCondition(condition);
            opposite = Condition.EQUAL; // Zero -> false
        }
        subroutine.addJump(opposite, startLabel);

        subroutine.addLabel(endLabel);
    }
}
