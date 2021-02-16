package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.staticanalysis.JavaScope;

import java.util.Collections;
import java.util.List;

public class JavaBlock implements JavaCode {
    public final List<JavaCode> codes;

    public JavaBlock(List<JavaCode> body) {
        this.codes = Collections.unmodifiableList(body);
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        JavaScope scope = JavaScope.newChildScope(parent, this);
        for (JavaCode code : codes) {
            code.compileCode(subroutine, scope);
        }
        scope.outOfScope();
    }
}
