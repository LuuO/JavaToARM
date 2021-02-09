package javatoarm.java;

public class JavaIfElse implements JavaCode {
    JavaExpression condition;
    JavaCode bodyTrue, bodyFalse;

    public JavaIfElse(JavaExpression condition, JavaCode bodyTrue, JavaCode bodyFalse) {
        this.condition = condition;
        this.bodyTrue = bodyTrue;
        this.bodyFalse = bodyFalse;
    }
}
