package javatoarm.variable;

import javatoarm.JTAException;
import javatoarm.assembly.Register;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.javaast.type.JavaType;
import javatoarm.javaast.type.PrimitiveType;

/**
 * Represents an immediate value in Java code
 */
public class Immediate implements Variable {
    private final JavaType type;
    private final Object value;
    private Register temp = null;

    /**
     * Construct an instance of Immediate to represent an immediate value
     *
     * @param type           type of the value
     * @param immediateValue the value
     */
    public Immediate(JavaType type, Object immediateValue) {
        this.type = type;
        this.value = immediateValue;
    }

    /**
     * Compare the number of bits required to represent the value and the provided bits.
     *
     * @return true if representing the value requires less than the input number of bits.
     */
    public boolean numberOfBitsLessThan(int bits) throws JTAException {
        if (type.equals(PrimitiveType.BOOLEAN) || type.equals(PrimitiveType.NULL)) {
            return bits >= 1;
        } else if (type.equals(PrimitiveType.INT) || type.equals(PrimitiveType.LONG)
                || type.equals(PrimitiveType.SHORT) || type.equals(PrimitiveType.BYTE)) {

            if (bits >= 64) {
                return true;
            }
            long maxNum = 1L << bits;
            long value = valueToLong();
            return value < maxNum && -value <= maxNum;
        } else if (type.equals(PrimitiveType.VOID)) {
            throw new IllegalArgumentException();
        } else {
            return false;
        }
    }

    /**
     * Convert value to its number representation in binary.
     * e.g. null -> 0, true -> 1, false -> 0, numbers -> numbers
     * The value must fit within 32 bits.
     *
     * @return number representation of the value in binary
     */
    public int toNumberRep() throws JTAException {
        if (type.equals(PrimitiveType.NULL)) {
            return 0;
        } else if (type.equals(PrimitiveType.BOOLEAN)) {
            return (Boolean) value ? 1 : 0;
        } else if (type.equals(PrimitiveType.INT)) {
            return (Integer) value;
        } else if (type.equals(PrimitiveType.LONG)) {
            long longValue = (Long) value;
            if (longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE) {
                throw new IllegalArgumentException();
            }
            return (int) longValue;
        } else if (type.equals(PrimitiveType.SHORT)) {
            return (Short) value;
        } else if (type.equals(PrimitiveType.BYTE)) {
            return (Byte) value;
        } else if (type.equals(PrimitiveType.VOID)) {
            throw new IllegalArgumentException();
        } else {
            throw new JTAException.NotImplemented(type.toString());
        }
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

    @Override
    public void delete() {
        if (temp != null) {
            temp.release();
            temp = null;
        }
    }

    @Override
    public void deleteIfIsTemp() {
        if (temp != null) {
            temp.release();
            temp = null;
        }
    }

    /**
     * Return the value in long
     *
     * @return the value, in long
     * @throws JTAException if an error occurs
     */
    private long valueToLong() throws JTAException {
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BYTE:
                    return (Byte) value;
                case SHORT:
                    return (Short) value;
                case INT:
                    return (Integer) value;
                case LONG:
                    return (Long) value;
            }
        }
        // TODO: support other types
        throw new JTAException.NotImplemented("valueToLong: " + type);
    }
}
