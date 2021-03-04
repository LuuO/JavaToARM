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

    /**
     * Add all instructions in the subroutine to this compiler
     *
     * @param subroutine the subroutine
     */
    void commitSubroutine(Subroutine subroutine);

    String toCompleteProgram(String starterClass, int stackPosition) throws JTAException;

    InstructionSet instructionSet();

}
