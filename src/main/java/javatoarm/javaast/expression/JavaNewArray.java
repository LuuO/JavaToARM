package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.type.JavaType;
import javatoarm.javaast.type.PrimitiveType;
import javatoarm.javaast.type.UserDefinedType;
import javatoarm.staticanalysis.JavaScope;
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
        this.numberOfElements = new ImmediateExpression(PrimitiveType.INT, elements.size());
    }

    public JavaExpression memorySize() throws JTAException {
        if (!(type instanceof UserDefinedType)) {
            throw new UnsupportedOperationException();
        }
        ImmediateExpression size = new ImmediateExpression(PrimitiveType.INT, type.size());
        return new NumericExpression(ArithmeticOperator.Multi.MULTIPLY, numberOfElements, size);
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}
