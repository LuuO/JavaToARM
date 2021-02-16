package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.type.JavaType;

public class JavaEnhancedForLoop implements JavaCode {
    public final JavaType elementType;
    public final String elementName;
    public final JavaExpression collection;
    public final JavaCode body;

    public JavaEnhancedForLoop(JavaType elementType, String elementName, JavaExpression collection, JavaCode body) {
        this.elementType = elementType;
        this.elementName = elementName;
        this.collection = collection;
        this.body = body;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
