package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;

/**
 * Super type of all Java execution code.
 */
public interface JavaCode {

    static String labelUID(Object obj) {
        String string = obj.toString();
        return string.substring(string.indexOf('@') + 1);
    }

    void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException;
}
