package javatoarm.arm;

import javatoarm.JTAException;
import javatoarm.assembly.*;
import javatoarm.token.operator.ArithmeticOperator;
import javatoarm.token.operator.LogicalNot;
import javatoarm.token.operator.OperatorToken;
import javatoarm.token.operator.PlusMinus;
import javatoarm.variable.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of Subroutine for ARMv7
 */
public class ARMSubroutine implements Subroutine {
    private final static Register[] R = ARMLibrary.Registers;
    private final static List<Register> callerSave = List.of(0, 1, 2, 3, 14).stream()
            .map(i -> R[i]).collect(Collectors.toList());
    private final static List<Register> arguments = List.of(0, 1, 2, 3).stream()
            .map(i -> R[i]).collect(Collectors.toList());

    private final Map<String, Integer> constants;
    private final RegisterAssigner ra;
    private StringBuilder text;
    private String finalized;

    /**
     * Constructs an instance of ARMSubroutine
     */
    public ARMSubroutine() {
        this.ra = new RegisterAssigner(InstructionSet.ARMv7);
        this.constants = new HashMap<>();
        this.text = new StringBuilder();
    }

    /**
     * Prepare the variable and get a register containing the value of the variable
     *
     * @param source the variable to prepare
     * @return a register representing the variable
     * @throws JTAException if an error occurs
     */
    private Register use(Variable source) throws JTAException {
        if (source instanceof LocalVariable || source instanceof TemporaryVariable) {
            return source.getRegister(ra);

        } else if (source instanceof Immediate) {
            Immediate imm = ((Immediate) source);
            Register register = imm.getRegister(ra);

            if (imm.numberOfBitsLessThan(8)) {
                ARMInstruction.move(text, Condition.ALWAYS, register, imm.toNumberRep());
            } else {
                Integer immValue = imm.toNumberRep();
                String label = "constant_" + immValue;
                constants.put(label, immValue);
                ARMInstruction.load(text, register, label);
                ARMInstruction.load(text, register, register);
            }
            return register;

        } else if (source instanceof MemoryOffset) {
            MemoryOffset memoryOffset = ((MemoryOffset) source);
            Register result = memoryOffset.getRegister(ra);
            Register array = use(memoryOffset.base);
            Register index = use(memoryOffset.offset);
            ARMInstruction.load(text, result, array, index, memoryOffset.leftShift);
            return result;

        } else {
            throw new JTAException.NotImplemented("use()");
        }
    }

    /**
     * Store the value in the source register in the target variable
     *
     * @param source the source register
     * @param target the target variable
     * @throws JTAException if an error occurs
     */
    private void store(Register source, Variable target) throws JTAException {
        if (target instanceof LocalVariable || target instanceof TemporaryVariable) {
            Register targetReg = target.getRegister(ra);
            ARMInstruction.move(text, Condition.ALWAYS, targetReg, source);

        } else if (target instanceof MemoryOffset) {
            MemoryOffset memoryOffset = (MemoryOffset) target;
            int shift = memoryOffset.leftShift;
            Register base = use(memoryOffset.base);
            Register index = use(memoryOffset.offset);
            ARMInstruction.store(text, Condition.ALWAYS, source, base, index, shift);

        } else {
            throw new JTAException.NotImplemented("store()");
        }
    }

    @Override
    public void addReturn(Variable returnValue) throws JTAException {
        Register value = use(returnValue);
        ARMInstruction.move(text, Condition.ALWAYS, R[0], value);
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
                       Variable result) throws JTAException {
        Register leftReg = use(left);
        Register resultRegister = result.getRegister(ra);
        if (operator instanceof PlusMinus) {
            OP op = operator == PlusMinus.PLUS ? OP.ADD : OP.SUB;

            // TODO magic number
            if (right instanceof Immediate && ((Immediate) right).numberOfBitsLessThan(8)) {
                ARMInstruction.instruction(
                        text, op, resultRegister, leftReg, ((Immediate) right).toNumberRep());
            } else {
                Register rightReg = use(right);
                ARMInstruction.instruction(text, op, resultRegister, leftReg, rightReg);
            }

        } else if (operator instanceof ArithmeticOperator.Multi) {
            Register rightReg = use(right);
            ARMInstruction.instruction(text, OP.MUL, resultRegister, leftReg, rightReg);

        } else {
            throw new UnsupportedOperationException();
        }
        store(resultRegister, result);
    }

    @Override
    public void addALU(OperatorToken.Unary operator, Variable operand, Variable result) throws JTAException {
        Register src = use(operand);
        Register dest = result.getRegister(ra);
        if (operator instanceof LogicalNot) {
            ARMInstruction.instruction(text, OP.CMP, src, 0);
            ARMInstruction.move(text, Condition.EQUAL, dest, 1);
            ARMInstruction.move(text, Condition.UNEQUAL, dest, 0);
        } else if (operator instanceof PlusMinus) {
            if (operator == PlusMinus.MINUS) {
                ARMInstruction.move(text, Condition.ALWAYS, dest, 0);
                ARMInstruction.instruction(text, OP.SUB, dest, dest, src);
            }
            /* else do nothing */
        } else {
            throw new JTAException.NotImplemented("Unary operator");
        }
        store(dest, result);
    }

    @Override
    public void addAssignment(Variable dest, Variable source) throws JTAException {
        Register src = use(source);
        store(src, dest);
    }

    @Override
    public void addCompare(Variable left, Variable right) throws JTAException {
        Register leftReg = use(left);

        if (right instanceof Immediate &&
                ((Immediate) right).numberOfBitsLessThan(8)) {

            ARMInstruction.instruction(text, OP.CMP, leftReg, ((Immediate) right).toNumberRep());

        } else {
            Register rightReg = use(right);
            ARMInstruction.instruction(text, OP.CMP, leftReg, rightReg);
        }
    }

    @Override
    public void addLogicalOperation(boolean saveResult, boolean isAnd, Variable left, Variable right,
                                    Variable result) throws JTAException {
        // TODO: bug fix - AND op requires left and right have matching bits when they are both non-zero
        ARMInstruction.instruction(text, isAnd ? OP.AND : OP.ORR, !saveResult,
                result.getRegister(ra), use(left), use(right));
        if (!saveResult) {
            result.deleteIfIsTemp();
        }
    }

    @Override
    public void addIncrementDecrement(Variable variable, boolean increase) throws JTAException {
        Register register = use(variable);
        OP op = increase ? OP.ADD : OP.SUB;
        ARMInstruction.instruction(text, op, register, register, 1);
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
        Register register = result.getRegister(ra);
        ARMInstruction.move(text, condition, register, 1);
        ARMInstruction.move(text, condition.opposite(), register, 0);
    }

    @Override
    public void checkCondition(Variable variable) throws JTAException {
        Register register = use(variable);
        ARMInstruction.instruction(text, OP.CMP, register, 0);
    }

    @Override
    public void addComment(String comment) {
        text.append("\t\t// ").append(comment).append('\n');
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
    public RegisterAssigner getRegisterAssigner() {
        return ra;
    }

    @Override
    public String toString() {
        if (text != null) {
            if (!constants.isEmpty()) {
                text.append(".data\n");
                constants
                        .forEach((label, value) -> ARMInstruction.labelValuePair(text, label, value));
                text.append(".text\n");
            }
            finalized = text.toString();
            text = null;
        }
        return finalized;
    }
}
