package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.javaast.type.JavaType;

public class Argument extends LocalVariable {
    public Argument(JavaScope holder, RegisterAssigner registerAssigner,
                    JavaType condition, String name) throws JTAException {
        super(holder, registerAssigner, condition, name, true);
    }
}
