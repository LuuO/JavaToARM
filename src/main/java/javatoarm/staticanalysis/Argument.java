package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.javaast.type.JavaType;

/**
 * Represent a local variable which comes from the argument of a function declaration.
 */
public class Argument extends LocalVariable {

    /**
     * Construct an instance of Argument
     *
     * @param holder           the holder scope of the new variable
     * @param registerAssigner the register assigner
     * @param type             the type
     * @param name             the name
     * @throws JTAException if error occurs
     */
    public Argument(JavaScope holder, RegisterAssigner registerAssigner,
                    JavaType type, String name) throws JTAException {

        super(holder, registerAssigner, type, name, true);
    }

}
