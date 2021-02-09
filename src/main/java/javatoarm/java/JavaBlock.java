package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.Register;
import javatoarm.RegisterAssigner;

import java.util.Collections;
import java.util.List;

public class JavaBlock extends JavaScope implements JavaCode {
    public final List<JavaCode> codes;

    public JavaBlock(JavaScope parent, List<JavaCode> body) {
        super(false, parent);
        this.codes = Collections.unmodifiableList(body);
    }

    public Register toAssembly(boolean returnRegister, JavaType returnType, RegisterAssigner registers
    ) throws JTAException {
        // 

        // if var delcar
        // declareVariable(type, name);
        // to use
        // getVariable(name);

        outOfScope();
        return null;
    }
}
