package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaScope;
import javatoarm.java.type.JavaSimpleType;
import javatoarm.java.type.JavaType;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.ArithmeticOperator;

import java.util.ArrayList;
import java.util.List;

public class JavaNewArray implements JavaExpression {
    public final JavaType type;
    public final JavaExpression numberOfElements;
    public final List<JavaExpression> initialElements;

    public JavaNewArray(JavaType type, JavaExpression numberOfElements) {
        this.type = type;
        this.numberOfElements = numberOfElements;
        this.initialElements = null;
    }

    public JavaNewArray(JavaType type, List<JavaExpression> elements) {
        this.type = type;
        this.initialElements = new ArrayList<>(elements);
        this.numberOfElements = new JavaImmediate(JavaSimpleType.INT, elements.size());
    }

    public JavaExpression memorySize() {
        if (!(type instanceof JavaSimpleType)) {
            throw new UnsupportedOperationException();
        }
        JavaImmediate size = new JavaImmediate(JavaSimpleType.INT, ((JavaSimpleType) type).size());
        return new NumericExpression(new ArithmeticOperator.Multiply(), numberOfElements, size);
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
