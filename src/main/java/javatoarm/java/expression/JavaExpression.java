package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaRightValue;
import javatoarm.java.JavaScope;
import javatoarm.staticanalysis.Variable;

public interface JavaExpression extends JavaRightValue {
    Variable compileExpression(Subroutine subroutine, JavaScope parent)
        throws JTAException;

    // TODO JavaExpression analyze type
}
