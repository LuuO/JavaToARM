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
import javatoarm.token.operator.ArithmeticOperator;
import javatoarm.token.operator.Logical;
import javatoarm.token.operator.OperatorToken;
import javatoarm.token.operator.PlusMinus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ARMSubroutine implements Subroutine {
    private final static Register[] R = ARMLibrary.Registers;
    private final static List<Register> callerSave = List.of(0, 1, 2, 3, 14).stream()
        .map(index -> new Register(index, InstructionSet.ARMv7)).collect(Collectors.toList());
    private final static List<Register> arguments = List.of(0, 1, 2, 3).stream()
        .map(index -> new Register(index, InstructionSet.ARMv7)).collect(Collectors.toList());
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
            Immediate imm = ((Immediate) source);
            TemporaryVariable temp = imm.getTemporary();
            ARMInstruction.move(text, Condition.ALWAYS, temp.getRegister(), (Integer) imm.value);
            return temp.getRegister();
        } else if (source instanceof MemoryOffset) {
            MemoryOffset memoryOffset = ((MemoryOffset) source);
            Register result = memoryOffset.getTemporary().getRegister();
            Register array = use(memoryOffset.array);
            Register index = use(memoryOffset.index);
            ARMInstruction.load(text, result, array, index, memoryOffset.shift);
            return result;
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
            Register targetReg = ((LocalVariable) target).getRegister();
            ARMInstruction.move(text, Condition.ALWAYS, targetReg, source);

        } else if (target instanceof MemoryOffset) {
            MemoryOffset memoryOffset = (MemoryOffset) target;
            Register base = use(memoryOffset.array);
            Register index = use(memoryOffset.index);
            int shift = memoryOffset.shift;

            ARMInstruction.store(text, Condition.ALWAYS, source, base, index, shift);

        } else if (target instanceof TemporaryVariable) {
            Register targetReg = ((TemporaryVariable) target).getRegister();
            ARMInstruction.move(text, Condition.ALWAYS, targetReg, source);

        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void addReturn(Variable returnValue) throws JTAException {
        Register value = use(returnValue);
        ARMInstruction.move(text, Condition.ALWAYS, R[0], value);
        returnValue.deleteIfIsTemp();
    }

    @Override
    public void addReturn() {
        ARMInstruction.popCalleeSave(text);
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
    public void addALU(OperatorToken.Binary operator, Variable left, Variable right,
                       Variable result)
        throws JTAException {
        Register leftReg = use(left);
        Register resultRegister = prepareStore(result);
        if (operator instanceof PlusMinus) {
            PlusMinus pm = (PlusMinus) operator;
            OP op = pm.isPlus ? OP.ADD : OP.SUB;

            if (right instanceof Immediate &&
                (Integer) ((Immediate) right).value < 0x800) {
                ARMInstruction.instruction(
                    text, op, resultRegister, leftReg, (Integer) ((Immediate) right).value);
            } else {
                Register rightReg = use(right);
                ARMInstruction.instruction(text, op, resultRegister, leftReg, rightReg);
            }

        } else if (operator instanceof ArithmeticOperator.Multiply) {
            Register rightReg = use(right);
            ARMInstruction.instruction(text, OP.MUL, resultRegister, leftReg, rightReg);
        } else {
            throw new UnsupportedOperationException();
        }
        left.deleteIfIsTemp();
        right.deleteIfIsTemp();
        store(resultRegister, result);
    }

    @Override
    public void addALU(OperatorToken operator, Variable operand, Variable result) {

    }

    @Override
    public void addAssignment(Variable left, Variable right) throws JTAException {
        Register src = use(right);
        store(src, left);
        left.deleteIfIsTemp();
        right.deleteIfIsTemp();
    }

    @Override
    public void addCompare(Variable left, Variable right) throws JTAException {
        Register leftReg = use(left);
        if (right instanceof Immediate &&
            (Integer) ((Immediate) right).value < 0x800) {

            ARMInstruction.instruction(text, OP.CMP, leftReg, (Integer) ((Immediate) right).value);

        } else {
            Register rightReg = use(right);
            ARMInstruction.instruction(text, OP.CMP, leftReg, rightReg);
        }

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
    public void addFunctionCall(String targetLabel, Register result, List<Variable> varArguments)
        throws JTAException {

        if (varArguments.size() > 4) {
            throw new UnsupportedOperationException();
        }

        List<Register> saved = new ArrayList<>(callerSave);
        saved.remove(result);
        ARMInstruction.push(text, saved);

        /* Move function arguments */
        List<Register> argumentsToPass = new ArrayList<>(varArguments.size());
        for (Variable argument : varArguments) {
            argumentsToPass.add(use(argument));
        }
        ARMInstruction.push(text, argumentsToPass);
        ARMInstruction.pop(text, arguments.subList(0, argumentsToPass.size()));

        ARMInstruction.branch(text, Condition.ALWAYS, OP.BL, targetLabel);
        if (result != null) {
            ARMInstruction.move(text, Condition.ALWAYS, result, R[0]);
        }
        ARMInstruction.pop(text, saved);
    }

    @Override
    public void saveBooleanResult(Condition condition, Variable result) throws JTAException {
        Register register = prepareStore(result);
        ARMInstruction.move(text, condition, register, 1);
        ARMInstruction.move(text, condition.opposite(), register, 0);
        result.deleteIfIsTemp();
    }

    @Override
    public void checkCondition(Variable condition) throws JTAException {
        Register register = use(condition);
        ARMInstruction.instruction(text, OP.CMP, register, 0);
        condition.deleteIfIsTemp();
    }

    @Override
    public void addComment(String comment) {
        text.append("\t\t//").append(comment).append('\n');
    }

    @Override
    public void addEmptyLine() {
        text.append('\n');
    }

    @Override
    public void pushCalleeSave() {
        ARMInstruction.pushCalleeSave(text);
    }

    @Override
    public void malloc(Variable size, Register result) throws JTAException {
        List<Register> saved = new ArrayList<>(callerSave);
        saved.remove(result);
        ARMInstruction.push(text, saved);
        ARMInstruction.move(text, Condition.ALWAYS, R[0], use(size));
        ARMInstruction.branch(text, Condition.ALWAYS, OP.BL, "_malloc");
        ARMInstruction.move(text, Condition.ALWAYS, result, R[0]);
        ARMInstruction.pop(text, saved);
    }

    @Override
    public String toString() {
        return text.toString();
    }
}
