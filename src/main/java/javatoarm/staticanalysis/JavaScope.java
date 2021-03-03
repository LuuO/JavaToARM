package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.javaast.JavaClass;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.JavaFunction;
import javatoarm.javaast.statement.VariableDeclareStatement;
import javatoarm.javaast.type.JavaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represent an object that managers the resources accessible to the member of some scopes.
 * Accessible resources include variables, functions, registers, class fields and loops to break.
 * Always invoke {@link JavaScope#outOfScope()} when the scope is out.
 */
public class JavaScope {
    public final JavaScope parent;
    public final JavaCode breakable;
    public final RegisterAssigner registerAssigner;
    private final Map<String, LocalVariable> variables;
    private final JavaClass javaClass;
    private final JavaFunction function;

    private JavaScope(JavaScope parent, JavaCode breakable, JavaClass javaClass, JavaFunction function,
                      RegisterAssigner registerAssigner) {
        this.variables = new HashMap<>();
        this.parent = parent;
        this.breakable = breakable;
        this.javaClass = javaClass;
        this.function = function;
        this.registerAssigner = registerAssigner;
    }

    private JavaScope(JavaScope parent, JavaCode breakable) {
        this.variables = new HashMap<>();
        this.parent = parent;
        this.breakable = breakable;
        this.javaClass = parent.javaClass;
        this.function = parent.function;
        this.registerAssigner = parent.registerAssigner;
    }

    /**
     * Create a child scope of some other scope
     *
     * @param parent the parent scope
     * @return the created scope
     */
    public static JavaScope newChildScope(JavaScope parent) {
        return new JavaScope(parent, parent.breakable);
    }

    /**
     * Create a breakable child scope (loop or switch) of some other scope.
     *
     * @param parent    the parent scope
     * @param breakable the breakable loop or switch
     * @return the created scope
     */
    public static JavaScope newChildScope(JavaScope parent, JavaCode breakable) {
        return new JavaScope(parent, breakable);
    }

    /**
     * Create a new scope representing a class.
     *
     * @param javaClass        the parent scope
     * @param registerAssigner the registerAssigner
     * @return the created class scope
     */
    public static JavaScope newClassScope(JavaClass javaClass, RegisterAssigner registerAssigner) {
        return new JavaScope(null, null,
                javaClass, null, registerAssigner);
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
                                             List<VariableDeclareStatement> arguments)
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

    /**
     * Try obtaining the variable with the provided name
     *
     * @param name the name of the variable
     * @return if the variable is reachable, returns the variable. Otherwise, returns null.
     */
    private LocalVariable tryGetVariable(String name) {
        LocalVariable var = variables.get(name);
        if (var == null && parent != null) {
            return parent.tryGetVariable(name);
        }
        return var;
    }

    /**
     * Get the return type of the function with a provided name and a list of argument types
     *
     * @param name          the name of the function
     * @param argumentTypes the list of argument types of the function
     * @return the return type of the function
     * @throws JTAException if the function does not exist or an error occurs
     */
    public final JavaType getFunctionReturnType(String name, List<JavaType> argumentTypes)
            throws JTAException {

        JavaFunction.Signature functionSignature = new JavaFunction.Signature(name, argumentTypes);
        return javaClass.getFunctionReturnType(functionSignature);
    }

    /**
     * Returns the function epilogue label for return statements
     *
     * @return the function epilogue label
     */
    public final String getEpilogueLabel() {
        return function.epilogueLabel;
    }

    /**
     * Declare a new variable in this scope
     *
     * @param type the type of the new variable
     * @param name the name of the new variable
     * @return a newly created LocalVariable representing the variable
     * @throws JTAException if some other non-class variable with the same name is already declared or an error occurs
     */
    public final LocalVariable declareVariable(JavaType type, String name) throws JTAException {
        LocalVariable existed = tryGetVariable(name);
        if (existed != null && existed.holder.javaClass != null) {
            throw new JTAException.VariableAlreadyDeclared(name);
        }

        LocalVariable var = new LocalVariable(this, registerAssigner, type, name);
        variables.put(name, var);
        return var;
    }

    /**
     * Declares function arguments
     */
    private void declareArguments(List<VariableDeclareStatement> argumentDeclares) throws JTAException {
        for (VariableDeclareStatement declare : argumentDeclares) {
            String name = declare.name();
            Argument argument =
                    new Argument(this, registerAssigner, declare.type(), name);
            variables.put(name, argument);
        }
    }
}
