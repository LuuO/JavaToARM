package javatoarm.assembly;

import javatoarm.JTAException;

public interface Compiler {

    void markGlobalLabel(String label);

    void addJumpLabel(String label);

    void addLabel(String label);

    Subroutine newSubroutine();

    void commitSubroutine(Subroutine subroutine);

    void addEmptyLine();

    String toCompleteProgram(String starterClass, int stackPosition) throws JTAException;
}
