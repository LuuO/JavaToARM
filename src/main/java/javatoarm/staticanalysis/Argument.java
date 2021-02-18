package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.javaast.type.JavaType;

/**
 * Represent a local variable which comes from the argument of a function declaration.
 */
public class Argument extends LocalVariable {

    public Argument(JavaScope holder, RegisterAssigner registerAssigner,
                    JavaType condition, String name) throws JTAException {
        super(holder, registerAssigner, condition, name, true);
    }
}
