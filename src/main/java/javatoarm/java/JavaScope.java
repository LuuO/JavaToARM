package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.InstructionSet;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.java.statement.JavaVariableDeclare;
import javatoarm.staticanalysis.Argument;
import javatoarm.staticanalysis.LocalVariable;

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
    private final JavaFunction function;

    // JavaCode owner - breakable
    private JavaScope(JavaScope parent, JavaCode owner, JavaClass javaClass, JavaFunction function,
                      RegisterAssigner registerAssigner) {
        this.variables = new HashMap<>();
        this.parent = parent;
        this.owner = owner;
        this.javaClass = javaClass;
        this.function = function;
        this.registerAssigner = registerAssigner;
    }

    private JavaScope(JavaScope parent, JavaCode owner) {
        this.variables = new HashMap<>();
        this.parent = parent;
        this.owner = owner;
        this.javaClass = parent.javaClass;
        this.function = parent.function;
        this.registerAssigner = parent.registerAssigner;
    }

    public static JavaScope newChildScope(JavaScope parent, JavaCode owner) {
        return new JavaScope(parent, owner);
    }

    public static JavaScope newClassScope(JavaClass javaClass, InstructionSet is) {
        return new JavaScope(null, null,
            javaClass, null, new RegisterAssigner(is));
    }

    public static JavaScope newFunctionScope(JavaScope classScope, JavaFunction function,
                                             List<JavaVariableDeclare> arguments)
        throws JTAException {

        JavaScope functionScope =
            new JavaScope(classScope, null, classScope.javaClass,
                function, classScope.registerAssigner);
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

    public final String getEpilogueLabel() {
        return function.epilogueLabel;
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
