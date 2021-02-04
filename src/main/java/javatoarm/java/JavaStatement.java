package javatoarm.java;

public interface JavaStatement extends JavaCode {
    JavaStatement BREAK = new JavaStatement() { };
    JavaStatement RETURN = new JavaStatement() { };

    class Return implements JavaStatement {
        public Return(JavaExpression expression) {

        }

        public Return() {

        }
    }
}
