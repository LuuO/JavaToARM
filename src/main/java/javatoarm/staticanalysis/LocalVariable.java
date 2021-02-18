package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.javaast.type.JavaType;

/**
 * Represent a local variable in a function body.
 */
public class LocalVariable implements Variable {
    public final JavaType condition;
    public final String name;
    public final JavaScope holder;
    private final RegisterAssigner registerAssigner;
    private final Register register;
    private boolean isDeleted = false;

    /**
     * Initiate a local variable which does not come from function declaration
     *
     * @param holder           the holder scope of the new variable
     * @param registerAssigner the register assigner
     * @param type             the type
     * @param name             the name
     */
    public LocalVariable(JavaScope holder, RegisterAssigner registerAssigner,
                         JavaType type, String name) throws JTAException {
        this(holder, registerAssigner, type, name, false);
    }

    protected LocalVariable(JavaScope holder, RegisterAssigner registerAssigner, JavaType condition,
                            String name, boolean isArgument)
            throws JTAException {
        this.registerAssigner = registerAssigner;
        this.holder = holder;
        this.condition = condition;
        this.name = name;

        if (isArgument) {
            this.register = registerAssigner.requestArgumentRegister(this);
        } else {
            this.register = registerAssigner.request(this);
        }
    }

    public Register getRegister() {
        if (isDeleted) {
            throw new UnsupportedOperationException("LocalVariable is already deleted.");
        }
        return register;
    }

    public void delete() {
        if (!isDeleted) {
            registerAssigner.release(register);
            isDeleted = true;
        }
    }

    @Override
    public void deleteIfIsTemp() {

    }

    @Override
    public JavaType getType() {
        return condition;
    }
}
