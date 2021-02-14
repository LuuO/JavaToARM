package javatoarm.java;

import javatoarm.java.expression.JavaExpression;
import javatoarm.java.expression.JavaImmediate;
import javatoarm.java.expression.JavaName;

import java.util.Collections;
import java.util.Map;

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
