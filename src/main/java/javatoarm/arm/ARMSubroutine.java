package javatoarm.arm;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Register;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Logical;
import javatoarm.token.operator.OperatorToken;

public class ARMSubroutine implements Subroutine {
    private final StringBuilder text;

    public ARMSubroutine() {
        text = new StringBuilder();
    }

    private void use(Variable source) {

    }

    private void store(int register, Variable target) throws JTAException {

    }

    @Override
    public void addReturn(Variable returnValue) {

        addReturn();
    }

    @Override
    public void addReturn() {

    }

    @Override
    public void addLabel(String label) {
        ARMInstruction.label(text, label);
    }

    @Override
    public void addJump(Condition condition, String targetLabel) {

    }

    @Override
    public void addALU(OperatorToken operator, Variable left, Variable right, Variable result) {

    }

    @Override
    public void addALU(OperatorToken operator, Variable operand, Variable result) {

    }

    @Override
    public void addAssignment(Variable left, Variable right) {

    }

    @Override
    public void addCompare(Variable left, Variable right) {

    }

    @Override
    public void addLogic(Logical logicalOperator, Variable variable) {

    }

    @Override
    public void checkType(OperatorToken operator, Variable left, Variable right, Variable result)
        throws JTAException {

    }

    @Override
    public void addIncrementDecrement(Variable variable, boolean increase) {

    }

    @Override
    public void addFunctionCall(String targetLabel, Register result) {
        ARMInstruction.pushCallerSave(text);
        ARMInstruction.branch(text, ARMInstruction.OP.BL, targetLabel);
        if (result != null) {
            ARMInstruction.instruction(text, ARMInstruction.OP.MOV, 0, result.index);
        }
        ARMInstruction.popCallerSave(text);
    }

    @Override
    public void saveComparisonResult(Condition condition, Variable result) {

    }

    @Override
    public void checkCondition(Variable condition) {

    }
}
