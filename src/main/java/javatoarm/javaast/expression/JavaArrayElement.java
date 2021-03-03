package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaLeftValue;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.MemoryOffset;
import javatoarm.staticanalysis.Variable;

/**
 * Represents an array element.
 * <p>
 * Examples: a[i], arr[0]
 * </p>
 * <p>
 * TODO: currently supports only int[] and boolean[]
 * </p>
 */
public class JavaArrayElement implements JavaLeftValue, JavaExpression {
    private final JavaExpression array, index;

    /**
     * Constructs an instance of JavaArrayElement
     *
     * @param array the array to find an element from
     * @param index the index of the element in the array
     */
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
}
