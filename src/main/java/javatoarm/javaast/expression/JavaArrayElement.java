package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaLeftValue;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.MemoryOffset;
import javatoarm.staticanalysis.Variable;

public class JavaArrayElement implements JavaLeftValue, JavaExpression {
    private final JavaExpression array, index;

    public JavaArrayElement(JavaExpression array, JavaExpression index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable array = this.array.compileExpression(subroutine, parent);
        Variable index = this.index.compileExpression(subroutine, parent);
        return new MemoryOffset(array, index, 2, parent.registerAssigner);
    }

    // TODO: improve
}
