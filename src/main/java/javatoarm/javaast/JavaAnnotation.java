package javatoarm.javaast;

import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaMember;

/**
 * An annotation in Java
 */
public class JavaAnnotation {
    public final JavaMember annotationType;
    public final JavaExpression arguments;

    /**
     * Create an annotation with arguments
     *
     * @param annotationType name of the annotation
     * @param arguments      arguments
     */
    public JavaAnnotation(JavaMember annotationType, JavaExpression arguments) {
        this.annotationType = annotationType;
        this.arguments = arguments;
    }

    /**
     * Create an annotation without arguments
     *
     * @param annotationType name of the annotation
     */
    public JavaAnnotation(JavaMember annotationType) {
        this.annotationType = annotationType;
        this.arguments = null;
    }

}
