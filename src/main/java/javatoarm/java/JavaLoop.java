package javatoarm.java;

public class JavaLoop extends JavaScope implements JavaCode {
    JavaStatement initial;
    JavaExpression condition;
    JavaStatement increment;
    JavaCode body;
    boolean isDoWhile;

    private JavaLoop(JavaCode body, JavaStatement initial, JavaExpression condition, JavaStatement increment, boolean isDoWhile) {
        super(false, null); //TODO
        this.initial = initial;
        this.condition = condition;
        this.increment = increment;
        this.isDoWhile = isDoWhile;
        this.body = body;
    }

    public static JavaLoop forLoop(JavaCode body, JavaStatement initial, JavaExpression condition, JavaStatement increment) {
        return new JavaLoop(body, initial, condition, increment, false);
    }

    public static JavaLoop whileLoop(JavaCode body, JavaExpression condition) {
        return new JavaLoop(body, null, condition, null, false);
    }

    public static JavaLoop doWhileLoop(JavaCode body, JavaExpression condition) {
        return new JavaLoop(body, null, condition, null, true);
    }
}
