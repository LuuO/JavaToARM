package javatoarm.javaast.control;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaBlock;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.statement.JavaVariableDeclare;
import javatoarm.staticanalysis.JavaScope;

import java.util.List;

public class JavaTryBlock implements JavaCode {
    public final JavaBlock tryBlock;
    public final List<JavaVariableDeclare> exceptions;
    public final JavaBlock catchBlock;

    public JavaTryBlock(JavaBlock tryBlock,
                        List<JavaVariableDeclare> exceptions, JavaBlock catchBlock) {
        this.tryBlock = tryBlock;
        this.exceptions = exceptions;
        this.catchBlock = catchBlock;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        throw new UnsupportedOperationException();
    }
}