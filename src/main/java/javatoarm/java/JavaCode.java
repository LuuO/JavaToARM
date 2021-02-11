package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;

/**
 * Super type of all Java execution code.
 */
public interface JavaCode {
    void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException;

    static String labelUID(Object obj) {
        String string = obj.toString();
        return string.substring(string.indexOf('@') + 1);
    }
}
