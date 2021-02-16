package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.expression.JavaExpression;

public class JavaSynchronized implements JavaCode {
    public final JavaExpression lock;
    public final JavaBlock body;

    public JavaSynchronized(JavaExpression lock, JavaBlock body) {
        this.lock = lock;
        this.body = body;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
