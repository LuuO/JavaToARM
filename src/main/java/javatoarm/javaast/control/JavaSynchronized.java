package javatoarm.javaast.control;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaBlock;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.variable.JavaScope;

/**
 * Represents a synchronized block
 */
public class JavaSynchronized implements JavaCode {
    public final JavaExpression lock;
    public final JavaBlock body;

    /**
     * Create an instance of synchronized block
     *
     * @param lock the lock
     * @param body the body
     */
    public JavaSynchronized(JavaExpression lock, JavaBlock body) {
        this.lock = lock;
        this.body = body;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) {
        throw new JTAException.NotImplemented("JavaSynchronized");
    }
}
