package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.java.JavaType;

public class Immediate implements Variable {
    public final JavaType type;
    public final Object value;
    public final RegisterAssigner registerAssigner;
    private TemporaryVariable temp = null;

    public Immediate(JavaType type, Object immediateValue, RegisterAssigner registerAssigner) {
        this.type = type;
        this.value = immediateValue;
        this.registerAssigner = registerAssigner;
    }

    public TemporaryVariable getTemporary() throws JTAException {
        if (temp == null) {
            temp = new TemporaryVariable(registerAssigner, type);
        }
        return temp;
    }

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public void delete() {
        if (temp != null) {
            temp.delete();
            temp = null;
        }
    }

    @Override
    public boolean deleteIfIsTemp() {
        if (temp != null) {
            boolean isTemp = temp.deleteIfIsTemp();
            temp = null;
            return isTemp;
        }
        return true;
    }
}
