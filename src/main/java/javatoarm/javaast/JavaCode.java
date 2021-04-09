package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.variable.JavaScope;

/**
 * Super type of all Java execution codes.
 */
public interface JavaCode {

    /**
     * Get an unique identification code for an object
     * TODO: This method is not reliable as hash codes are not unique, should use an UUID generator singleton instead.
     *
     * @param obj the object
     * @return the unique identification code
     */
    static int getUniqueID(Object obj) {
        return obj.hashCode();
    }

    /**
     * Compiles this code. If an object implements both {@link JavaCode}
     * and {@link JavaExpression}, only one of this method and
     * {@link JavaExpression#compileExpression(Subroutine, JavaScope)} should be called.
     *
     * @param subroutine the subroutine which this code belongs to
     * @param parent     the parent scope of this code
     * @throws JTAException if an error occurs
     */
    void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException;
}
