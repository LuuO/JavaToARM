package javatoarm.arm;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.InstructionSet;
import javatoarm.assembly.Register;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.Immediate;
import javatoarm.staticanalysis.LocalVariable;
import javatoarm.staticanalysis.MemoryOffset;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Logical;
import javatoarm.token.operator.OperatorToken;

public class ARMSubroutine implements Subroutine {
    private final static Register R0 = new Register(0, InstructionSet.ARMv7);
    private final StringBuilder text;

    public ARMSubroutine() {
        text = new StringBuilder();
    }

    private Register use(Variable source) throws JTAException {
        if (source instanceof LocalVariable) {
            return ((LocalVariable) source).getRegister();
        } else if (source instanceof TemporaryVariable) {
            return ((TemporaryVariable) source).getRegister();
        } else if (source instanceof Immediate) {
            TemporaryVariable temp = ((Immediate) source).getTemporary();
            ARMInstruction.move(text, Condition.ALWAYS, R0, temp.getRegister());
            return temp.getRegister();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private Register prepareStore(Variable target) throws JTAException {
        if (target instanceof LocalVariable) {
            return ((LocalVariable) target).getRegister();
        } else if (target instanceof MemoryOffset) {
            return ((MemoryOffset) target).getTemporary().getRegister();
        } else if (target instanceof TemporaryVariable) {
            return ((TemporaryVariable) target).getRegister();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void store(Register source, Variable target) throws JTAException {
        if (target instanceof LocalVariable) {
            Register register = ((LocalVariable) target).getRegister();
            ARMInstruction.move(text, Condition.ALWAYS, source, register);

        } else if (target instanceof MemoryOffset) {
            MemoryOffset memoryOffset = (MemoryOffset) target;
            Register base = use(memoryOffset.array);
            Register index = use(memoryOffset.index);
            int shift = memoryOffset.shift;

            ARMInstruction.store(text, Condition.ALWAYS, source, base, index, shift);

        } else if (target instanceof TemporaryVariable) {
            Register register = ((TemporaryVariable) target).getRegister();
            ARMInstruction.move(text, Condition.ALWAYS, source, register);

        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void addReturn(Variable returnValue) throws JTAException {
        Register value = use(returnValue);
        ARMInstruction.move(text, Condition.ALWAYS, R0, value);
        addReturn();
        returnValue.deleteIfIsTemp();
    }

    @Override
    public void addReturn() {
        ARMInstruction.returnInstruction(text);
    }

    @Override
    public void addLabel(String label) {
        ARMInstruction.label(text, label);
    }

    @Override
    public void addJump(Condition condition, String targetLabel) {
        ARMInstruction.branch(text, condition, OP.B, targetLabel);
    }

    @Override
    public void addALU(OperatorToken operator, Variable left, Variable right, Variable result) {

    }

    @Override
    public void addALU(OperatorToken operator, Variable operand, Variable result) {

    }

    @Override
    public void addAssignment(Variable left, Variable right) throws JTAException {
        Register src = use(left);
        store(src, right);
        left.deleteIfIsTemp();
        right.deleteIfIsTemp();
    }

    @Override
    public void addCompare(Variable left, Variable right) throws JTAException {
        Register leftReg = use(left);
        Register rightReg = use(right);
        ARMInstruction.instruction(text, OP.CMP, leftReg, rightReg);
        left.deleteIfIsTemp();
        right.deleteIfIsTemp();
    }

    @Override
    public void addLogic(Logical logicalOperator, Variable variable) {

    }

    @Override
    public void addIncrementDecrement(Variable variable, boolean increase) throws JTAException {
        Register register = use(variable);
        OP op = increase ? OP.ADD : OP.SUB;
        ARMInstruction.instruction(text, op, register, register, 1);
        variable.deleteIfIsTemp();
    }

    @Override
    public void addFunctionCall(String targetLabel, Register result) {
        ARMInstruction.pushCallerSave(text);
        ARMInstruction.branch(text, Condition.ALWAYS, OP.BL, targetLabel);
        if (result != null) {
            ARMInstruction.move(text, Condition.ALWAYS, result, R0);
        }
        ARMInstruction.popCallerSave(text);
    }

    @Override
    public void saveBooleanResult(Condition condition, Variable result) throws JTAException {
        Register register = prepareStore(result);
        ARMInstruction.move(text, condition, register, 1);
        ARMInstruction.move(text, condition.opposite(), register, 0);
        result.deleteIfIsTemp();
    }

    @Override
    public void checkCondition(Variable condition) {

    }
}
