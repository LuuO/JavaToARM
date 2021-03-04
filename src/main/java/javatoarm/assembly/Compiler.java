package javatoarm.assembly;

import javatoarm.JTAException;
import javatoarm.arm.ARMCompiler;

/**
 * Compilers are used to generate ISA-dependent instructions.
 */
public interface Compiler {

    /**
     * Get a compiler for the provided instruction set
     *
     * @param instructionSet instruction set
     * @return the compiler for the provided instruction set
     */
    static Compiler getCompiler(InstructionSet instructionSet) {
        if (instructionSet == InstructionSet.ARMv7) {
            return new ARMCompiler();
        } else {
            throw new JTAException.NotImplemented(instructionSet.toString());
        }
    }

    /**
     * Mark a label as global so it can be called from outside the file
     *
     * @param label the label to make
     */
    void markGlobalLabel(String label);

    /**
     * Add a jump or branch instruction
     *
     * @param target target label
     */
    void addJump(String target);

    /**
     * Add a label
     *
     * @param label the label
     */
    void addLabel(String label);

    /**
     * Initialize a new subroutine.
     *
     * @return the new subroutine
     */
    Subroutine newSubroutine();

    /**
     * Add all instructions in the subroutine to this compiler
     *
     * @param subroutine the subroutine
     */
    void commitSubroutine(Subroutine subroutine);

    /**
     * Get the compiled assembly codes
     *
     * @param entryClass    the class containing the main method, which is the entry point of the program
     * @param stackPosition initial value of the stack pointer
     * @return the compiled instructions
     * @throws JTAException if an error occurs
     */
    String toCompleteAssembly(String entryClass, int stackPosition) throws JTAException;

}
