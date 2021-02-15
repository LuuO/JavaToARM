package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.expression.JavaExpression;

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
