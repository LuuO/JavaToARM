package javatoarm;

import javatoarm.java.JavaScope;
import javatoarm.java.JavaType;

public class Variable {

    public final boolean isTemporary;
    public final JavaType type;
    public final String name;
    public final JavaScope holder;
    private final RegisterAssigner registerAssigner;
    private final Register register;
    private Location location;
    private int address;

    private boolean isDeleted = false;

    /**
     * Initiate a variable
     *
     * @param holder
     * @param registerAssigner
     * @param type
     * @param name
     */
    public Variable(JavaScope holder, RegisterAssigner registerAssigner, JavaType type, String name)
            throws JTAException {
        this.isTemporary = false;
        this.registerAssigner = registerAssigner;
        this.holder = holder;
        this.type = type;
        this.name = name;
        this.isDeleted = false;

        register = registerAssigner.request(this);
    }

    /**
     * Get an instance of a temporary variable. Always invoke {#deleteIfIsTemp} after it is used.
     *
     * @param registerAssigner the Register Assigner
     * @param type             the type of variable
     */
    public static Variable getTemporary(RegisterAssigner registerAssigner, JavaType type)
            throws JTAException {
        return new Variable(null, registerAssigner, type, "temp");
    }

    /**
     * Get an instance of a temporary variable. Always invoke {#deleteIfIsTemp} after it is used.
     *
     * @param registerAssigner the Register Assigner
     * @param type             the type of variable
     */
    public static Variable getGlobal(RegisterAssigner registerAssigner, JavaType type)
            throws JTAException {
        return new Variable(null, registerAssigner, type, "temp");
    }

    public final void delete() {
        switch (location) {
            case REGISTER -> registerAssigner.release(register);
            case STACK, HEAP -> throw new UnsupportedOperationException("Not implemented");
        }
        isDeleted = true;
    }

    /**
     * Delete this variable if it is temporary. Always call this method after the variable is used.
     *
     * @return true if this is a temporary variable, false otherwise.
     */
    public final boolean deleteIfIsTemp() {
        if (isTemporary) {
            delete();
        }
        return isTemporary;
    }

    public final Register getRegister() {
        if (isDeleted) {
            throw new UnsupportedOperationException("Variable is already deleted.");
        }
        return register;
    }

    /**
     * Represent where the variable is stored.
     */
    public enum Location {
        REGISTER, STACK, HEAP
    }

}
