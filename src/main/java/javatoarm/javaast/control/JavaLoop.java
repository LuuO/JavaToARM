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

/**
 * Represents a loop in Java
 */
public abstract class JavaLoop implements JavaCode {
    public final String endLabel, startLabel, conditionLabel;

    private final JavaExpression condition;
    private final JavaCode body;

    /**
     * Constructs a JavaLoop
     *
     * @param condition the condition to evaluate
     * @param body      the loop body to execute
     */
    public JavaLoop(JavaExpression condition, JavaCode body) {
        this.condition = condition;
        this.body = body;

        String uid = String.valueOf(JavaCode.getUniqueID(this));
        endLabel = "loop_" + uid + "_end";
        startLabel = "loop_" + uid + "_start";
        conditionLabel = "loop_" + uid + "_condition";
    }

    /**
     * Add a break statement to the subroutine
     *
     * @param subroutine the subroutine
     */
    public void addBreak(Subroutine subroutine) {
        subroutine.addJump(Condition.ALWAYS, endLabel);
    }

    /**
     * Add a continue statement to the subroutine
     *
     * @param subroutine the subroutine
     */
    public void addContinue(Subroutine subroutine) {
        subroutine.addJump(Condition.ALWAYS, conditionLabel);
    }

    /**
     * Compiles the header of the loop
     *
     * @param subroutine the subroutine that this loop belongs to
     */
    protected void compileStart(Subroutine subroutine) {
        subroutine.addEmptyLine();
        subroutine.addComment("loop");
    }

    /**
     * Compiles the body of the loop
     *
     * @param subroutine the subroutine that this loop belongs to
     * @param loopScope  the scope of this loop
     */
    protected void compileBody(Subroutine subroutine, JavaScope loopScope) throws JTAException {
        subroutine.addLabel(startLabel);
        body.compileCode(subroutine, loopScope);
    }

    /**
     * Compiles the end of the loop, which includes condition
     * evaluation and conditional jump to the start.
     *
     * @param subroutine the subroutine that this loop belongs to
     * @param loopScope  the scope of this loop
     */
    protected void compileEnd(Subroutine subroutine, JavaScope loopScope) throws JTAException {
        subroutine.addLabel(conditionLabel);
        Condition jumpCondition;

        if (condition instanceof ComparisonExpression) {
            ComparisonExpression comparison = (ComparisonExpression) condition;
            comparison.compileToConditionCode(subroutine, loopScope);
            jumpCondition = comparison.getCondition();
        } else {
            Variable condition = this.condition.compileExpression(subroutine, loopScope);
            subroutine.checkCondition(condition);
            condition.deleteIfIsTemp();
            jumpCondition = Condition.UNEQUAL; // Non-Zero -> true
        }

        subroutine.addJump(jumpCondition, startLabel);
        subroutine.addLabel(endLabel);
        subroutine.addEmptyLine();
    }

    /**
     * A for-loop
     */
    public static class For extends JavaLoop {
        private final JavaStatement initial, increment;

        /**
         * Constructs a for-loop
         *
         * @param initial   the initial statement
         * @param condition the condition to evaluate
         * @param increment the increment statement
         * @param body      the body
         */
        public For(JavaStatement initial, JavaExpression condition, JavaStatement increment, JavaCode body) {
            super(condition, body);
            this.initial = initial;
            this.increment = increment;
        }

        @Override
        public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
            JavaScope loopScope = JavaScope.newBreakableScope(parent, this);
            compileStart(subroutine);
            if (initial != null) {
                initial.compileCode(subroutine, loopScope);
            }
            subroutine.addJump(Condition.ALWAYS, conditionLabel);
            compileBody(subroutine, loopScope);
            if (increment != null) {
                increment.compileCode(subroutine, loopScope);
            }
            compileEnd(subroutine, loopScope);
        }
    }

    /**
     * A while-loop
     */
    public static class While extends JavaLoop {

        /**
         * Constructs a while-loop
         *
         * @param condition the condition to evaluate
         * @param body      the loop body to execute
         */
        public While(JavaExpression condition, JavaCode body) {
            super(condition, body);
        }

        @Override
        public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
            JavaScope loopScope = JavaScope.newBreakableScope(parent, this);
            compileStart(subroutine);
            subroutine.addJump(Condition.ALWAYS, conditionLabel);
            compileBody(subroutine, loopScope);
            compileEnd(subroutine, loopScope);
        }
    }

    /**
     * A do-while-loop
     */
    public static class DoWhile extends JavaLoop {

        /**
         * Constructs a do-while-loop
         *
         * @param condition the condition to evaluate
         * @param body      the loop body to execute
         */
        public DoWhile(JavaExpression condition, JavaCode body) {
            super(condition, body);
        }

        @Override
        public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
            JavaScope loopScope = JavaScope.newBreakableScope(parent, this);
            compileStart(subroutine);
            compileBody(subroutine, loopScope);
            compileEnd(subroutine, loopScope);
        }
    }
}

