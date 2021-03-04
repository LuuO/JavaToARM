package javatoarm.arm;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;

import java.util.List;
import java.util.Scanner;

/**
 * An implementation of Compile for ARMv7
 */
public class ARMCompiler implements Compiler {
    private final StringBuilder text;

    /**
     * Constructs an instance of ARMCompiler
     */
    public ARMCompiler() {
        text = new StringBuilder();
    }

    @Override
    public String toString() {
        return text.toString();
    }

    @Override
    public void markGlobalLabel(String label) {
        text.append(".global\t").append(label).append('\n');
    }

    @Override
    public void addJump(String target) {
        ARMInstruction.branch(text, Condition.ALWAYS, OP.B, target);
    }

    @Override
    public void addLabel(String label) {
        ARMInstruction.label(text, label);
    }

    @Override
    public Subroutine newSubroutine() {
        return new ARMSubroutine();
    }

    @Override
    public void commitSubroutine(Subroutine subroutine) {
        if (!(subroutine instanceof ARMSubroutine)) {
            throw new IllegalArgumentException();
        }
        text.append(subroutine);
    }

    @Override
    public String toCompleteAssembly(String entryClass, int stackPosition) throws JTAException {
        String classLabel = "class_" + entryClass;
        String javaFile = toString();
        int mainOffset = findOffsetTo(javaFile, classLabel, "function_main");

        return ARMLibrary.start(classLabel, mainOffset, stackPosition,
                List.of(ARMLibrary.heapInit())) +
                ARMLibrary.malloc() +
                javaFile +
                ARMLibrary.heapStartLabel();
    }

    /**
     * Find the memory offset (in bytes) between the starting location of the class and the
     * branch instruction to specific function.
     *
     * @param javaFile      the file containing the function
     * @param className     label of the class
     * @param classFunction label of the function
     * @return the offset from the class label to the branch instruction to the function, in bytes
     * @throws JTAException if an error occurs
     */
    private int findOffsetTo(String javaFile, String className, String classFunction) throws JTAException {
        Scanner scanner = new Scanner(javaFile);
        String line = scanner.nextLine();
        while (!line.startsWith(className)) {
            if (!scanner.hasNext()) {
                throw new JTAException.UnknownFunction(
                        "Cannot find %s in %s".formatted(classFunction, className));
            }
            line = scanner.nextLine();
        }

        int offset = 0;
        line = scanner.nextLine();
        while (!line.trim().endsWith(classFunction)) {
            line = scanner.nextLine();
            offset++;
            if (!line.trim().startsWith("B")) {
                throw new JTAException.UnknownFunction(
                        "Cannot find %s in %s".formatted(classFunction, className));
            }
        }
        return offset * 4;
    }
}
