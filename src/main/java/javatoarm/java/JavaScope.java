package javatoarm.java;

import javatoarm.ISA;
import javatoarm.JTAException;
import javatoarm.RegisterAssigner;
import javatoarm.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class JavaScope {
    public final boolean isClass;
    private final Map<String, Variable> variables;
    private final JavaScope parent;
    private final RegisterAssigner registerAssigner;

    protected JavaScope(boolean isClass, JavaScope parent) {
        this.isClass = isClass;
        this.variables = new HashMap<>();
        this.parent = parent;
        if (parent != null) {
            registerAssigner = parent.registerAssigner;
        } else {
            registerAssigner = new RegisterAssigner(ISA.ARM);
        }
    }

    public final void outOfScope() {
        variables.forEach((name, var) -> var.delete());
    }

    public final Variable getVariable(String name) {
        Variable var = variables.get(name);
        if (var == null && parent != null) {
            return parent.getVariable(name);
        }
        return var;
    }

    public final Variable declareVariable(JavaType type, String name) throws JTAException {
        Variable existed = getVariable(name);
        if (existed != null && !existed.holder.isClass) {
            throw new JTAException.VariableAlreadyDeclared(name);
        }

        Variable var = new Variable(this, registerAssigner, type, name);
        variables.put(name, var);
        return var;
    }

    public final Variable requestTemp(JavaType type) throws JTAException {
        return Variable.getTemporary(registerAssigner, type);
    }
}
