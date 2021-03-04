package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.Register;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.javaast.type.JavaType;

/**
 * Represents a variable inside a class
 */
public class Field implements Variable {
    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteIfIsTemp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaType getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Register getRegister(RegisterAssigner registerAssigner) {
        throw new UnsupportedOperationException();
    }
}
