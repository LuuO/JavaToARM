package javatoarm.variable;

import javatoarm.JTAException;
import javatoarm.assembly.Register;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.javaast.type.JavaType;

/**
 * Represent a variable that is stored in the memory and constitutes of a base variable and an offset variable.
 * Examples include an element of an array.
 */
public class MemoryOffset implements Variable {
    public final JavaType type;
    public final Variable base, offset;
    public final int leftShift;
    private Register temp;
    private boolean isDeleted;

    /**
     * Construct an MemoryOffset variable.
     * The address of the variable will be (base + (offset << leftShift)).
     *
     * @param base      the base variable
     * @param offset    the offset variable
     * @param leftShift amount of left shift during address calculation
     */
    public MemoryOffset(Variable base, Variable offset, int leftShift) {
        this.base = base;
        this.offset = offset;
        this.type = base.getType(); // TODO: get the correct type
        this.leftShift = leftShift;
        this.temp = null;
        this.isDeleted = false;
    }

    @Override
    public void delete() {
        if (isDeleted) {
            throw new IllegalArgumentException("Already deleted");
        }
        if (temp != null) {
            temp.release();
            temp = null;
        }
        base.delete();
        offset.delete();
        isDeleted = true;
    }

    @Override
    public void deleteIfIsTemp() {
        if (isDeleted) {
            throw new IllegalArgumentException("Already deleted");
        }
        if (temp != null) {
            temp.release();
            temp = null;
        }
        base.deleteIfIsTemp();
        offset.deleteIfIsTemp();
    }

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public Register getRegister(RegisterAssigner registerAssigner) throws JTAException {
        if (temp == null) {
            temp = registerAssigner.requestRegister();
            temp.assign(this);
        }
        return temp;
    }
}
