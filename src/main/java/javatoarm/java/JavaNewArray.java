package javatoarm.java;

import javatoarm.java.expression.JavaExpression;
import javatoarm.java.expression.JavaImmediate;
import javatoarm.java.expression.NumericExpression;
import javatoarm.token.operator.ArithmeticOperator;

public class JavaNewArray implements JavaRightValue {
    JavaType type;
    JavaExpression numberOfElements;

    public JavaNewArray(JavaType type, JavaExpression numberOfElements) {
        this.type = type;
        this.numberOfElements = numberOfElements;
    }

    public JavaExpression memorySize() {
        JavaImmediate size = new JavaImmediate(JavaType.INT, type.size());
        return new NumericExpression(new ArithmeticOperator.Multiply(), numberOfElements, size);
    }
}
