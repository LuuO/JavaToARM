package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.type.JavaType;
import javatoarm.javaast.type.PrimitiveType;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.TemporaryVariable;
import javatoarm.staticanalysis.Variable;
import javatoarm.token.operator.ArithmeticOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an array creation expression.
 */
public class NewArrayExpression implements JavaExpression {
    public final JavaType type;
    public final JavaExpression numberOfElements;
    public final List<JavaExpression> initialElements;

    /**
     * Creates an instance of NewArrayExpression
     *
     * @param type             type of the array
     * @param numberOfElements number of elements in the array
     */
    public NewArrayExpression(JavaType type, JavaExpression numberOfElements) {
        this.type = type;
        this.numberOfElements = numberOfElements;
        this.initialElements = null;
    }

    /**
     * Creates an instance of NewArrayExpression
     *
     * @param type     type of the array
     * @param elements list of elements in the array
     */
    public NewArrayExpression(JavaType type, List<JavaExpression> elements) {
        this.type = type;
        this.initialElements = new ArrayList<>(elements);
        this.numberOfElements = new ImmediateExpression(PrimitiveType.INT, elements.size());
    }

    /**
     * Get an expression which computes the memory size required to store the array, in bytes.
     */
    public JavaExpression memorySize() throws JTAException {
        if (!(type instanceof PrimitiveType)) {
            throw new JTAException.NotImplemented("memorySize for " + type);
        }
        ImmediateExpression size = new ImmediateExpression(PrimitiveType.INT, type.size());
        return new NumericExpression(ArithmeticOperator.Multi.MULTIPLY, numberOfElements, size);
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        Variable size = memorySize().compileExpression(subroutine, parent);
        TemporaryVariable result = subroutine.getTemporary(type);
        subroutine.malloc(size, result.getRegister(null));
        size.deleteIfIsTemp();
        return result;
    }
}
