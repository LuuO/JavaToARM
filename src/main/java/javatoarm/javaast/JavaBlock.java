package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.JavaScope;

import java.util.Collections;
import java.util.List;

/**
 * Represents a block of codes enclosed by { }
 */
public class JavaBlock implements JavaCode {
    public final List<JavaCode> codes;

    /**
     * Constructs a new block
     *
     * @param body list of codes within the block
     */
    public JavaBlock(List<JavaCode> body) {
        this.codes = Collections.unmodifiableList(body);
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        JavaScope scope = JavaScope.newChildScope(parent);
        for (JavaCode code : codes) {
            code.compileCode(subroutine, scope);
        }
        scope.outOfScope();
    }
}
