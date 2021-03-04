package javatoarm.assembly;

import javatoarm.JTAException;
import javatoarm.javaast.type.JavaType;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.OperatorToken;

import java.util.List;

/**
 * <p>Represents a subroutine in assembly</p>
 * <p>The user may call public methods of an instance of Subroutine to add instructions.
 * All instructions will appear in the same order as they are added. </p>
 */
public interface Subroutine {

    /**
     * Add return instructions with a return value
     *
     * @param returnValue the return value
     * @throws JTAException if error occurs
     */
    void addReturn(Variable returnValue) throws JTAException;

    /**
     * Add return instructions without a return value
     */
    void addReturn();

    /**
     * Add a label
     */
    void addLabel(String label);

    /**
     * Add a jump or branch instruction
     *
     * @param condition   condition
     * @param targetLabel target location
     */
    void addJump(Condition condition, String targetLabel);

    /**
     * Add instructions for a binary ALU operation
     * <p>TODO: use enum to replace OperatorToken</p>
     *
     * @param operator the operator
     * @param left     left operand
     * @param right    right operand
     * @param result   variable to store the result
     * @throws JTAException if an error occurs
     */
    void addALU(OperatorToken.Binary operator, Variable left, Variable right, Variable result)
            throws JTAException;

    /**
     * Add instructions for a unary ALU operation. This method
     * <p>TODO: use enum to replace OperatorToken</p>
     *
     * @param operator the operator
     * @param operand  the operand
     * @param result   variable to store the result
     * @throws JTAException if an error occurs
     */
    void addALU(OperatorToken.Unary operator, Variable operand, Variable result) throws JTAException;

    /**
     * Add instructions for an assigment operation
     *
     * @param dest   destination variable
     * @param source source variable
     * @throws JTAException if an error occurs
     */
    void addAssignment(Variable dest, Variable source) throws JTAException;

    /**
     * Add instructions for a comparison operation
     *
     * @param left  left variable
     * @param right right variable
     * @throws JTAException if an error occurs
     */
    void addCompare(Variable left, Variable right) throws JTAException;

    /**
     * Add instruction to perform an AND or OR on two boolean variables. The result of the
     * operation will be saved to the result variable, or the condition code will be changed
     * accordingly. Even if the user choose not to store the operation result to a variable,
     * the user still needs provided a temporary variable, since AND and OR operations on some
     * ISAs must have a destination register.
     *
     * @param saveResult if saveResult is true, the result of logical operation
     *                   will be saved to the result variable, and the condition
     *                   code will not be changed. if saveResult is false, the result
     *                   of logical operation will not be saved to the result
     *                   variable, and the condition code will be changed.
     * @param isAnd      true for AND operations, false for ORR operations
     * @param left       first boolean variable
     * @param right      second boolean variable
     * @param result     result variable. if saveResult is false,
     *                   result.deleteIfIsTemp() will be invoked.
     */
    void addLogicalOperation(boolean saveResult, boolean isAnd, Variable left, Variable right,
                             Variable result) throws JTAException;

    /**
     * Add instructions for an increment or decrement operation. The instructions will change the value
     * of the variable by 1.
     *
     * @param variable the variable to change
     * @param increase true to increase, false to decrease
     * @throws JTAException if an error occurs
     */
    void addIncrementDecrement(Variable variable, boolean increase) throws JTAException;

    void addFunctionCall(String targetLabel, Register result, List<Variable> arguments)
            throws JTAException;

    void saveBooleanResult(Condition condition, Variable result) throws JTAException;

    /**
     * Check if the variable holds t
     *
     * @param variable
     * @throws JTAException
     */
    void checkCondition(Variable variable) throws JTAException;

    /**
     * Add comment
     *
     * @param comment comment
     */
    void addComment(String comment);

    /**
     * Add an empty line to the instructions
     */
    void addEmptyLine();

    /**
     * Push all callee-saved registers onto the program stack.
     */
    void pushCalleeSave();

    /**
     * Allocate a block of memory in heap and store the starting address of the heap in a register
     *
     * @param size the size of the memory block
     * @param result register to store the starting address of the allocated memory
     * @throws JTAException if an error occurs
     */
    void malloc(Variable size, Register result) throws JTAException;

    RegisterAssigner getRegisterAssigner();

    /**
     *
     * @param type
     * @return
     * @throws JTAException
     */
    default TemporaryVariable getTemporary(JavaType type) throws JTAException {
        return new TemporaryVariable(type, getRegisterAssigner().requestRegister());
    }
}
