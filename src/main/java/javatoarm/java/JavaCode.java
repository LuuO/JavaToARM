package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;

/**
 * Super type of all Java execution code.
 */
public interface JavaCode {
    void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException;
}
