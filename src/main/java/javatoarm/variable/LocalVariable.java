package javatoarm.variable;

import javatoarm.assembly.Register;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.javaast.type.JavaType;

/**
 * Represent a local variable in a function body.
 */
public class LocalVariable implements Variable {
    public final JavaType type;
    public final String name;
    private final Register register;
    private boolean isDeleted = false;

    /**
     * Initiate a local variable in a function and
     * set the new local variable as the holder of the register.
     *
     * @param type     the type
     * @param name     the name
     * @param register the associated register
     */
    public LocalVariable(JavaType type, String name, Register register) {
        this.type = type;
        this.name = name;
        this.register = register;
    }

    @Override
    public void delete() {
        if (!isDeleted) {
            register.release();
            isDeleted = true;
        }
    }

    @Override
    public void deleteIfIsTemp() {
        /* do nothing */
    }

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public Register getRegister(RegisterAssigner registerAssigner) {
        if (isDeleted) {
            throw new UnsupportedOperationException("LocalVariable is already deleted.");
        }
        return register;
    }
}
