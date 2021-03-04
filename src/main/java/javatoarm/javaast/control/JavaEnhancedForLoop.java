package javatoarm.javaast.control;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.type.JavaType;
import javatoarm.variable.JavaScope;

/**
 * Represents an enhanced for-loop in Java.
 * Example: for (Integer i : listOfInt) { }
 */
public class JavaEnhancedForLoop implements JavaCode {
    public final JavaType elementType;
    public final String elementName;
    public final JavaExpression collection;
    public final JavaCode body;

    /**
     * Constructs an enhanced for-loop
     *
     * @param elementType element type
     * @param elementName name for the current element
     * @param collection  the collection to iterate
     * @param body        the loop body
     */
    public JavaEnhancedForLoop(JavaType elementType, String elementName, JavaExpression collection, JavaCode body) {
        this.elementType = elementType;
        this.elementName = elementName;
        this.collection = collection;
        this.body = body;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) {
        throw new JTAException.NotImplemented("JavaEnhancedForLoop");
    }
}
