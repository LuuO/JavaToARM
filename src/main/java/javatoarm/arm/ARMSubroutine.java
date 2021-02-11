package javatoarm.arm;

import javatoarm.Condition;
import javatoarm.JTAException;
import javatoarm.Register;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.LocalVariable;
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

    private void store(Variable source, Variable target) throws JTAException {

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

    }

    @Override
    public void saveComparisonResult(Condition condition, Variable result) {

    }

    @Override
    public void checkCondition(Variable condition) {

    }
}
