package javatoarm.java;

import javatoarm.java.expression.JavaExpression;
import javatoarm.java.expression.JavaImmediate;
import javatoarm.java.expression.NumericExpression;
import javatoarm.java.type.JavaSimpleType;
import javatoarm.java.type.JavaType;
import javatoarm.token.operator.ArithmeticOperator;

public class JavaNewArray implements JavaRightValue {
    JavaType type;
    JavaExpression numberOfElements;

    public JavaNewArray(JavaType type, JavaExpression numberOfElements) {
        this.type = type;
        this.numberOfElements = numberOfElements;
    }

    public JavaExpression memorySize() {
        if (!(type instanceof JavaSimpleType)) {
            throw new UnsupportedOperationException();
        }
        JavaImmediate size = new JavaImmediate(JavaSimpleType.INT, ((JavaSimpleType) type).size());
        return new NumericExpression(new ArithmeticOperator.Multiply(), numberOfElements, size);
    }
}
