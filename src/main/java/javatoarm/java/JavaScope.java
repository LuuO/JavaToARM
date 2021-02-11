package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.RegisterAssigner;
import javatoarm.assembly.InstructionSet;
import javatoarm.staticanalysis.Argument;
import javatoarm.staticanalysis.LocalVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class JavaScope {
    public final JavaScope parent;
    public final JavaCode owner;
    public final RegisterAssigner registerAssigner;
    private final Map<String, LocalVariable> variables;
    private final JavaClass javaClass;

    // JavaCode owner - breakable
    private JavaScope(JavaScope parent, JavaCode owner, JavaClass javaClass) {
        this.variables = new HashMap<>();
        this.parent = parent;
        this.owner = owner;
        this.javaClass = javaClass;
        if (parent != null) {
            registerAssigner = parent.registerAssigner;
        } else {
            registerAssigner = new RegisterAssigner(InstructionSet.ARMv7);
        }
    }

    public static JavaScope newChildScope(JavaScope parent, JavaCode owner) {
        return new JavaScope(parent, owner, parent.javaClass);
    }

    public static JavaScope newClassScope(JavaClass javaClass) {
        return new JavaScope(null, null, javaClass);
    }

    public static JavaScope newFunctionScope(JavaScope classScope,
                                             List<JavaVariableDeclare> arguments)
        throws JTAException {

        JavaScope functionScope = new JavaScope(classScope, null, classScope.javaClass);
        functionScope.declareArguments(arguments);
        return functionScope;
    }

    public final void outOfScope() {
        variables.forEach((name, var) -> var.delete());
    }

    public final LocalVariable getVariable(String name) {
        LocalVariable var = variables.get(name);
        if (var == null && parent != null) {
            return parent.getVariable(name);
        }
        return var;
    }

    public final JavaType getFunctionReturnType(String name, List<JavaType> argumentTypes)
        throws JTAException {
        // TODO: check arguments
        return javaClass.getFunctionInterface(name).returnType;
    }

    public final LocalVariable declareVariable(JavaType type, String name) throws JTAException {
        LocalVariable existed = getVariable(name);
        if (existed != null && existed.holder.javaClass != null) {
            throw new JTAException.VariableAlreadyDeclared(name);
        }

        LocalVariable var = new LocalVariable(this, registerAssigner, type, name);
        variables.put(name, var);
        return var;
    }

    private void declareArguments(List<JavaVariableDeclare> argumentDeclares)
        throws JTAException {
        for (JavaVariableDeclare declare : argumentDeclares) {
            String name = declare.name();
            Argument argument =
                new Argument(this, registerAssigner, declare.type(), name);
            variables.put(name, argument);
        }
    }
}
