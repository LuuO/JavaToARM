package javatoarm.assembly;

import javatoarm.JTAException;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Logical;
import javatoarm.token.operator.OperatorToken;

public interface Subroutine {
    void addReturn(Variable returnValue) throws JTAException;

    void addReturn();

    void addLabel(String label);

    void addJump(Condition condition, String targetLabel);

    // TODO: use enum to replace OperatorToken
    void addALU(OperatorToken operator, Variable left, Variable right, Variable result);

    void addALU(OperatorToken operator, Variable operand, Variable result);

    void addAssignment(Variable left, Variable right) throws JTAException;

    // Variable getImmediate(JavaType type, Object value);
    void addCompare(Variable left, Variable right) throws JTAException;

    void addLogic(Logical logicalOperator, Variable variable);

    void addIncrementDecrement(Variable variable, boolean increase) throws JTAException;

    void addFunctionCall(String targetLabel, Register result);

    void saveBooleanResult(Condition condition, Variable result) throws JTAException;

    void checkCondition(Variable condition);

}
