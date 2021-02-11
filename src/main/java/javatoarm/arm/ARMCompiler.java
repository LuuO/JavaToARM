package javatoarm.arm;

import javatoarm.assembly.Compiler;
import javatoarm.assembly.Subroutine;

import java.util.List;

public class ARMCompiler implements Compiler {
    List<String> globalLabels;
    List<String> jumpTable;
    private final StringBuilder text;

    public ARMCompiler() {
        text = new StringBuilder();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void markGlobalLabel(String label) {
        text.append(".global\t").append(label).append('\n');
    }

    @Override
    public void addJumpLabel(String label) {
        text.append("\tB\t").append(label).append('\n');
    }

    @Override
    public void addLabel(String label) {
        text.append(label).append(':').append('\n');
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
}
