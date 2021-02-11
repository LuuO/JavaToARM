package javatoarm.arm;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.Condition;
import javatoarm.assembly.Subroutine;

import java.util.List;
import java.util.Scanner;

public class ARMCompiler implements Compiler {
    private final StringBuilder text;
    List<String> globalLabels;
    List<String> jumpTable;

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
    public void addEmptyLine() {
        text.append('\n');
    }

    @Override
    public String toCompleteProgram(String starterClass, int stackPosition)
        throws JTAException {
        StringBuilder program = new StringBuilder();
        String javaFile = toString();
        int mainOffset = findOffsetTo(javaFile, "class_" + starterClass, "function_main");

        program.append(".global\t_start\n_start:\n");
        program.append("\t\tLDR\tR0, =stack_start\n");
        program.append("\t\tLDR\tSP, [R0]\n");
        program.append("\t\tLDR\tR0, =%s\n".formatted("class_" + starterClass));
        program.append("\t\tADD\tR0, R0, #%d\n".formatted(mainOffset));
        program.append("\t\tBLX\tR0\n");
        program.append("IDLE:\n");
        program.append("\t\tB\tIDLE\n");
        program.append("stack_start:\t.word %d\n\n\n\n".formatted(stackPosition));

        program.append(javaFile);

        program.append("heap:\n");
        program.append("\n");
        return program.toString();
    }

    private int findOffsetTo(String javaFile, String className, String classFunction)
        throws JTAException.UnknownFunction {
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
        return offset;
    }
}
