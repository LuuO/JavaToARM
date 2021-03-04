package javatoarm.javaast.control;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaBlock;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.statement.VariableDeclareStatement;
import javatoarm.variable.JavaScope;

import java.util.List;

/**
 * Represents a try-catch block.
 */
public class JavaTryBlock implements JavaCode {
    public final JavaBlock tryBlock;
    public final List<VariableDeclareStatement> exceptions;
    public final JavaBlock catchBlock;

    /**
     * Create an instance of JavaTryBlock
     *
     * @param tryBlock   the try block
     * @param exceptions exceptions to catch
     * @param catchBlock block to execute after an exception is caught
     */
    public JavaTryBlock(JavaBlock tryBlock, List<VariableDeclareStatement> exceptions,
                        JavaBlock catchBlock) {
        this.tryBlock = tryBlock;
        this.exceptions = exceptions;
        this.catchBlock = catchBlock;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) {
        throw new JTAException.NotImplemented("JavaTryBlock");
    }
}
