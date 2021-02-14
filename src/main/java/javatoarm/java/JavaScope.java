package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.InstructionSet;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.java.statement.JavaVariableDeclare;
import javatoarm.java.type.JavaType;
import javatoarm.staticanalysis.Argument;
import javatoarm.staticanalysis.LocalVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represent an object that managers the resources accessible to the member of some scopes.
 * Accessible resources include variables, functions, registers, class fields and loops to break.
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

    /**
     * Create a child scope of some other scope
     *
     * @param parent the parent scope
     * @param owner
     * @return the created scope
     */
    public static JavaScope newChildScope(JavaScope parent, JavaCode owner) {
        return new JavaScope(parent, owner);
    }


    public static JavaScope newClassScope(JavaClass javaClass, InstructionSet is) {
        return new JavaScope(null, null,
            javaClass, null, new RegisterAssigner(is));
    }

    /**
     * Create a scope of some function
     *
     * @param classScope the scope of the class that contains the function
     * @param function   the function
     * @param arguments  arguments of the function
     * @return the created scope
     * @throws JTAException if error occurs
     */
    public static JavaScope newFunctionScope(JavaScope classScope, JavaFunction function,
                                             List<JavaVariableDeclare> arguments)
        throws JTAException {

        JavaScope functionScope =
            new JavaScope(classScope, null, classScope.javaClass,
                function, classScope.registerAssigner);
        functionScope.declareArguments(arguments);
        return functionScope;
    }

    /**
     * Notify the scope that it has gone out of scope.
     * This method should always be invoked when it goes out of scope.
     */
    public final void outOfScope() {
        variables.forEach((name, var) -> var.delete());
    }

    /**
     * Search a variable with the given name
     *
     * @param name name of the variable
     * @return the variable found
     * @throws JTAException.InvalidName if the variable is not found.
     */
    public final LocalVariable getVariable(String name) throws JTAException.InvalidName {
        LocalVariable variable = tryGetVariable(name);
        if (variable == null) {
            throw new JTAException.InvalidName("Unknown variable name " + name);
        }
        return variable;
    }

    private LocalVariable tryGetVariable(String name) {
        LocalVariable var = variables.get(name);
        if (var == null && parent != null) {
            return parent.tryGetVariable(name);
        }
        return var;
    }


    public final JavaType getFunctionReturnType(String name, List<JavaType> argumentTypes)
        throws JTAException {
        // TODO: check arguments
        JavaFunction.Interface functionInterface = new JavaFunction.Interface(name, argumentTypes);
        return javaClass.getFunctionReturnType(functionInterface);
    }

    public final String getEpilogueLabel() {
        return function.epilogueLabel;
    }

    public final LocalVariable declareVariable(JavaType type, String name) throws JTAException {
        LocalVariable existed = tryGetVariable(name);
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
