package javatoarm.assembly;

import javatoarm.JTAException;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.OperatorToken;

import java.util.List;

public interface Subroutine {
    void addReturn(Variable returnValue) throws JTAException;

    void addReturn();

    void addLabel(String label);

    void addJump(Condition condition, String targetLabel);

    // TODO: use enum to replace OperatorToken
    void addALU(OperatorToken.Binary operator, Variable left, Variable right, Variable result)
        throws JTAException;

    void addALU(OperatorToken operator, Variable operand, Variable result);

    void addAssignment(Variable left, Variable right) throws JTAException;

    // Variable getImmediate(JavaType type, Object value);
    void addCompare(Variable left, Variable right) throws JTAException;

    /**
     * Add instruction to perform an AND or OR on two boolean variables. The result of the
     * operation will be saved to the result variable, or the condition code will be changed
     * accordingly. Even if the user choose not to store the operation result to a variable,
     * the user still needs provided a temporary variable, since AND and OR operations on some
     * ISAs must have a destination register.
     *
     * @param saveResult if saveResult is 1, the result of logical operation
     *                   will be saved to the result variable, and the condition
     *                   code will not be changed. if saveResult is 0, the result
     *                   of logical operation will not be saved to the result
     *                   variable, and the condition code will be changed.
     * @param isAnd      true for AND operations, false for ORR operations
     * @param left       first boolean variable
     * @param right      second boolean variable
     * @param result     result variable. if saveResult is false,
     *                   result.deleteIfIsTemp() will be invoked.
     */
    void addLogic(boolean saveResult, boolean isAnd, Variable left, Variable right,
                  Variable result) throws JTAException;

    void addIncrementDecrement(Variable variable, boolean increase) throws JTAException;

    void addFunctionCall(String targetLabel, Register result, List<Variable> arguments)
        throws JTAException;

    void saveBooleanResult(Condition condition, Variable result) throws JTAException;

    void checkCondition(Variable condition) throws JTAException;

    void addComment(String comment);

    void addEmptyLine();

    void pushCalleeSave();

    void malloc(Variable size, Register result) throws JTAException;
}
