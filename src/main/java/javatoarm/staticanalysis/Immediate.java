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

    /**
     * Compare the number of bits required to represent the value and the provided bits.
     *
     * @return true if representing the value requires less than the input number of bits.
     */
    public boolean numberOfBitsLessThan(int bits) {
        if (type.equals(JavaType.BOOL) || type.equals(JavaType.NULL)) {
            return bits >= 1;
        } else if (type.equals(JavaType.INT) || type.equals(JavaType.LONG)
            || type.equals(JavaType.SHORT) || type.equals(JavaType.BYTE)
            || type.equals(JavaType.FLOAT) || type.equals(JavaType.DOUBLE)) {

            if (bits >= 64) {
                return true;
            }
            long maxNum = 1L << bits;
            int value = (Integer) this.value;
            return value < maxNum && -value <= maxNum;
        } else if (type.equals(JavaType.VOID)) {
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
    public int toNumberRep() {
        if (type.equals(JavaType.NULL)) {
            return 0;
        } else if (type.equals(JavaType.BOOL)) {
            return (Boolean) value ? 1 : 0;
        } else if (type.equals(JavaType.INT)) {
            return (Integer) value;
        } else if (type.equals(JavaType.LONG)) {
            long longValue = (Long) value;
            if (longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE) {
                throw new IllegalArgumentException();
            }
            return (int) longValue;
        } else if (type.equals(JavaType.SHORT)) {
            return (Short) value;
        } else if (type.equals(JavaType.BYTE)) {
            return (Byte) value;
        } else if (type.equals(JavaType.VOID)) {
            throw new IllegalArgumentException();
        } else {
            throw new UnsupportedOperationException();
        }
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
    public void deleteIfIsTemp() {
        if (temp != null) {
            temp.deleteIfIsTemp();
            temp = null;
        }
    }
}
