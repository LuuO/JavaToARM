package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.javaast.type.JavaType;

/**
 * Represents a temporary variable.
 * Always invoke {@link TemporaryVariable#deleteIfIsTemp()} after using it.
 */
public class TemporaryVariable implements Variable {
    public final JavaType type;
    private final RegisterAssigner registerAssigner;
    private final Register register;
    private boolean isDeleted = false;

    /**
     * Create a new temporary variable
     *
     * @param registerAssigner the register assigner to get register from
     * @param type             type of variable
     * @throws JTAException if an error occurs
     */
    public TemporaryVariable(RegisterAssigner registerAssigner, JavaType type) throws JTAException {
        this.registerAssigner = registerAssigner;
        this.type = type;

        register = registerAssigner.request(this);
    }

    /**
     * Get the register held by this variable
     *
     * @return the register
     */
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
