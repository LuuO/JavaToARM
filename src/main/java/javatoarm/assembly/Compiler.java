package javatoarm.assembly;

public interface Compiler {

    void markGlobalLabel(String label);

    void addJumpLabel(String label);

    void addLabel(String label);

    Subroutine newSubroutine();

    void commitSubroutine(Subroutine subroutine);
}
