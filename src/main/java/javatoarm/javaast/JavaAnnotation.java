package javatoarm.javaast;

import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaName;

public class JavaAnnotation {
    public final JavaName name;
    public final JavaExpression arguments;

    public JavaAnnotation(JavaName name, JavaExpression arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public JavaAnnotation(JavaName name) {
        this.name = name;
        this.arguments = null;
    }


}
