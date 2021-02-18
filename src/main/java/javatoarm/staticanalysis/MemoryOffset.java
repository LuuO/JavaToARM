package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.javaast.type.JavaType;

/**
 * Represent a variable that is stored in the memory and constitutes of a base variable and an offset variable.
 * Examples include an element of an array.
 */
public class MemoryOffset implements Variable {
    public final JavaType type;
    public final Variable base, offset;
    public final RegisterAssigner registerAssigner;
    public final int leftShift;
    private TemporaryVariable temp = null;

    /**
     * Construct an MemoryOffset variable.
     * The address of the variable will be (base + (offset << leftShift)).
     *
     * @param base             the base variable
     * @param offset           the offset variable
     * @param leftShift        amount of left shift during address calculation
     * @param registerAssigner the register assigner
     */
    public MemoryOffset(Variable base, Variable offset, int leftShift,
                        RegisterAssigner registerAssigner) {
        this.base = base;
        this.offset = offset;
        this.registerAssigner = registerAssigner;
        this.type = base.getType(); // TODO: get the correct type
        this.leftShift = leftShift;
    }

    /**
     * Get a temporary variable
     *
     * @return a temporary variable
     * @throws JTAException if error occurs
     */
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
        base.delete();
        offset.delete();
    }

    @Override
    public void deleteIfIsTemp() {
        if (temp != null) {
            temp.delete();
            temp = null;
        }
        base.deleteIfIsTemp();
        offset.deleteIfIsTemp();
    }

    @Override
    public JavaType getType() {
        return type;
    }
}
