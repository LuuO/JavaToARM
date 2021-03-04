package javatoarm.javaast.expression;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaLeftValue;
import javatoarm.javaast.JavaRightValue;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.LocalVariable;
import javatoarm.staticanalysis.Variable;

import java.util.List;

/**
 * <p>
 * Represents a member in Java. A member typically appears as a string Java file.
 * It could be a variable name, a function name, or a type name.
 * </p><p>
 * A member is considered simple if it does not appears to be a member of some other member.
 * </p><p>
 * Examples of simple members: aaa, bbb, funcName
 * </p><p>
 * Examples of complex members: aaa.functionName, cc.dd.ee
 * </p>
 */
public class JavaMember implements JavaRightValue, JavaLeftValue, JavaExpression {
    public final List<String> path;

    public JavaMember(String name) {
        this.path = List.of(name);
    }

    public JavaMember(List<String> path) {
        if (path.size() == 0) {
            throw new IllegalArgumentException();
        }
        this.path = List.copyOf(path);
    }

    @Override
    public String toString() {
        return String.join(".", path);
    }

    /**
     * Check and convert this member to a string
     *
     * @return the string representing this member
     * @throws JTAException.InvalidName if this is not a simple member
     */
    public String toSimpleName() throws JTAException.InvalidName {
        if (!isSimple()) {
            throw new JTAException.InvalidName(toString() + " is not a valid simple name");
        }
        return path.get(0);
    }

    /**
     * Check if this is a simple member
     *
     * @return true if this is a simple member, false otherwise
     */
    public boolean isSimple() {
        return path.size() == 1;
    }

    @Override
    public Variable compileExpression(Subroutine subroutine, JavaScope parent) throws JTAException {
        if (path.size() > 1) {
            throw new JTAException.Unsupported("Member access is not supported yet");
        }
        return parent.getVariable(toSimpleName());
    }

}
