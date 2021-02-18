package javatoarm.javaast;

import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaName;

/**
 * An annotation in Java
 */
public class JavaAnnotation {
    public final JavaName name;
    public final JavaExpression arguments;

    /**
     * Create an annotation with arguments
     *
     * @param name      name of the annotation
     * @param arguments arguments
     */
    public JavaAnnotation(JavaName name, JavaExpression arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    /**
     * Create an annotation without arguments
     *
     * @param name name of the annotation
     */
    public JavaAnnotation(JavaName name) {
        this.name = name;
        this.arguments = null;
    }

}
