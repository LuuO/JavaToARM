package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;

public interface BooleanExpression extends JavaExpression {
    void compileToConditionCode(Subroutine parent, JavaScope scope) throws JTAException;

    Condition getCondition();
}
