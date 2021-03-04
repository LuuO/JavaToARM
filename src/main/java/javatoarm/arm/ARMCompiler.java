package javatoarm.arm;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;

import java.util.List;
import java.util.Scanner;

public class ARMCompiler implements Compiler {
    private final StringBuilder text;

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
    public void addJumpLabel(String label) {
        ARMInstruction.branch(text, Condition.ALWAYS, OP.B, label);
    }

    @Override
    public void addLabel(String label) {
        ARMInstruction.label(text, label);
    }

    @Override
    public Subroutine newSubroutine() throws JTAException {
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
    public String toCompleteProgram(String entryClass, int stackPosition)
            throws JTAException {
        String classLabel = "class_" + entryClass;
        String javaFile = toString();
        int mainOffset = findOffsetTo(javaFile, classLabel, "function_main");

        return ARMLibrary.start(classLabel, mainOffset, stackPosition,
                List.of(ARMLibrary.mallocInit())) +
                ARMLibrary.malloc() +
                javaFile +
                ARMLibrary.heapStartLabel();
    }

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
