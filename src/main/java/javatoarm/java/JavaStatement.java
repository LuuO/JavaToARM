package javatoarm.java;

import javatoarm.JTAException;

public interface JavaStatement extends JavaCode {
    JavaStatement BREAK = new JavaStatement() { };
    JavaStatement RETURN = new JavaStatement() { };

    class Return implements JavaStatement {
        public final JavaExpression returnValue;

        public Return(JavaExpression returnValue) {
            this.returnValue = returnValue;
        }

        public Return() {
            this.returnValue = null;
        }
    }
}
