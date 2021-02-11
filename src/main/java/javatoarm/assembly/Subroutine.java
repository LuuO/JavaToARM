package javatoarm.assembly;

import javatoarm.Condition;
import javatoarm.JTAException;
import javatoarm.Register;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.Logical;
import javatoarm.token.operator.OperatorToken;

public interface Subroutine {
    void addReturn(Variable returnValue);
    void addReturn();
    void addLabel(String label);
    void addJump(Condition condition, String targetLabel);

    // TODO: use enum to replace OperatorToken
    void addALU(OperatorToken operator, Variable left, Variable right, Variable result);
    void addALU(OperatorToken operator, Variable operand, Variable result);

    void addAssignment(Variable left, Variable right);
    // Variable getImmediate(JavaType type, Object value);
    void addCompare(Variable left, Variable right);
    void addLogic(Logical logicalOperator, Variable variable);
    void checkType(OperatorToken operator, Variable left, Variable right, Variable result) throws
        JTAException;
    void addIncrementDecrement(Variable variable, boolean increase);
    void addFunctionCall(String targetLabel, Register result);
    void saveComparisonResult(Condition condition, Variable result);

    void checkCondition(Variable condition);
}
