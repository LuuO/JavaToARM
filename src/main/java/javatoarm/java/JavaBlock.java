package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.Register;
import javatoarm.RegisterAssigner;

import java.util.ArrayList;
import java.util.List;

public class JavaBlock extends JavaScope implements JavaCode {
    List<JavaCode> codes = new ArrayList<>();

    protected JavaBlock(JavaScope parent) {
        super(false, parent);
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
