package javatoarm.assembly;

import javatoarm.JTAException;
import javatoarm.arm.ARMCompiler;

public interface Compiler {

    static Compiler getCompiler(InstructionSet instructionSet) {
        if (instructionSet == InstructionSet.ARMv7) {
            return new ARMCompiler();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    void markGlobalLabel(String label);

    void addJumpLabel(String label);

    void addLabel(String label);

    Subroutine newSubroutine();

    void commitSubroutine(Subroutine subroutine);

    void addEmptyLine();

    String toCompleteProgram(String starterClass, int stackPosition) throws JTAException;

    InstructionSet instructionSet();
}
