package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.javaast.type.JavaType;

public class MemoryOffset implements Variable {
    public final JavaType type;
    public final Variable array, index;
    public final RegisterAssigner registerAssigner;
    public final int shift;
    private TemporaryVariable temp = null;

    public MemoryOffset(Variable array, Variable index, int shift,
                        RegisterAssigner registerAssigner) {
        this.array = array;
        this.index = index;
        this.registerAssigner = registerAssigner;
        this.type = array.getType();
        this.shift = shift;
    }

    public TemporaryVariable getTemporary() throws JTAException {
        if (temp == null) {
            temp = new TemporaryVariable(registerAssigner, type);
        }
        return temp;
    }

    @Override
    public void delete() {
        if (temp != null) {
            temp.delete();
            temp = null;
        }
        array.delete();
        index.delete();
    }

    @Override
    public void deleteIfIsTemp() {
        if (temp != null) {
            temp.delete();
            temp = null;
        }
        array.deleteIfIsTemp();
        index.deleteIfIsTemp();
    }

    @Override
    public JavaType getType() {
        return type;
    }
}
