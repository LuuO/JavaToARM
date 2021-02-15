package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;

/**
 * Represents an expression that produces a boolean value
 */
public interface BooleanExpression extends JavaExpression {
    void compileToConditionCode(Subroutine parent, JavaScope scope) throws JTAException;

    Condition getCondition();
}
