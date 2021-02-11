package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.java.JavaType;

public class Immediate implements Variable {
    public final JavaType type;
    public final Object value;
    public final RegisterAssigner registerAssigner;

    public Immediate(JavaType type, Object immediateValue, RegisterAssigner registerAssigner) {
        this.type = type;
        this.value = immediateValue;
        this.registerAssigner = registerAssigner;
    }

    public TemporaryVariable getTemporary() throws JTAException {
        return new TemporaryVariable(registerAssigner, type);
    }

    @Override
    public JavaType getType() {
        return type;
    }
}
