package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.Register;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.java.JavaType;

public class TemporaryVariable implements Variable {
    public final JavaType type;
    private final RegisterAssigner registerAssigner;
    private final Register register;
    private boolean isDeleted = false;

    public TemporaryVariable(RegisterAssigner registerAssigner, JavaType type) throws JTAException {
        this.registerAssigner = registerAssigner;
        this.type = type;

        register = registerAssigner.request(this);
    }

    public Register getRegister() {
        if (isDeleted) {
            throw new UnsupportedOperationException("LocalVariable is already deleted.");
        }
        return register;
    }

    @Override
    public boolean deleteIfIsTemp() {
        delete();
        return true;
    }

    @Override
    public JavaType getType() {
        return type;
    }

    public void delete() {
        if (!isDeleted) {
            registerAssigner.release(register);
            isDeleted = true;
        }
    }
}
