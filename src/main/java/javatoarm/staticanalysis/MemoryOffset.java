package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.java.JavaType;

public class MemoryOffset implements Variable {
    public final JavaType type;
    public final Variable array, index;
    public final RegisterAssigner registerAssigner;

    public MemoryOffset(Variable array, Variable index, RegisterAssigner registerAssigner)
        throws JTAException {
        this.array = array;
        this.index = index;
        this.registerAssigner = registerAssigner;
        this.type = array.getType();
    }

    public TemporaryVariable getTemporary() throws JTAException {
        return new TemporaryVariable(registerAssigner, type);
    }

    @Override
    public JavaType getType() {
        return type;
    }
}
