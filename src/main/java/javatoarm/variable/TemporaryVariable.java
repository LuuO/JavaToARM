package javatoarm.variable;

import javatoarm.assembly.Register;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.javaast.type.JavaType;

/**
 * Represents a temporary variable.
 * Always invoke {@link TemporaryVariable#deleteIfIsTemp()} after using it.
 */
public class TemporaryVariable implements Variable {
    public final JavaType type;
    private final Register register;
    private boolean isDeleted = false;

    /**
     * Create a new temporary variable and set the new temporary
     * variable as the holder of the register.
     *
     * @param type     type of variable
     * @param register the associated register
     */
    public TemporaryVariable(JavaType type, Register register) {
        this.type = type;
        this.register = register;
        register.assign(this);
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
    public Register getRegister(RegisterAssigner ignored) {
        if (isDeleted) {
            throw new UnsupportedOperationException("LocalVariable is already deleted.");
        }
        return register;
    }

    @Override
    public void delete() {
        if (isDeleted) {
            throw new UnsupportedOperationException("LocalVariable is already deleted.");
        } else {
            register.release();
            isDeleted = true;
        }
    }
}
