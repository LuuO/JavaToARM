package javatoarm.java.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.java.JavaLeftValue;
import javatoarm.java.JavaRightValue;
import javatoarm.java.JavaScope;
import javatoarm.staticanalysis.LocalVariable;

import java.util.List;

//TODO bool
public final class JavaName implements JavaRightValue, JavaLeftValue, JavaExpression {
    public final List<String> path;

    public JavaName(String name) {
        this.path = List.of(name);
    }

    public JavaName(List<String> path) {
        if (path.size() == 0) {
            throw new IllegalArgumentException();
        }
        this.path = path;
    }

    @Override
    public String toString() {
        return String.join(".", path);
    }

    public String toSimpleName() throws JTAException {
        if (!isSimple()) {
            throw new JTAException.InvalidName(toString() + " is not a valid simple name");
        }
        return path.get(0);
    }

    public boolean isSimple() {
        return path.size() == 1;
    }

    @Override
    public LocalVariable compileExpression(Subroutine subroutine, JavaScope parent)
        throws JTAException {
        if (path.size() > 1) {
            throw new JTAException.Unsupported("Member access is not supported yet");
        }

        return parent.getVariable(toSimpleName());
    }

}
