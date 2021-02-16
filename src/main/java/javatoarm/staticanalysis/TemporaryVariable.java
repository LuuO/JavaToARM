package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.Register;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.javaast.type.JavaType;

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
    public void deleteIfIsTemp() {
        delete();
    }

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public void delete() {
        if (!isDeleted) {
            registerAssigner.release(register);
            isDeleted = true;
        }
    }
}
