package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.javaast.type.JavaSimpleType;
import javatoarm.javaast.type.JavaType;

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
        if (type.equals(JavaSimpleType.BOOL) || type.equals(JavaSimpleType.NULL)) {
            return bits >= 1;
        } else if (type.equals(JavaSimpleType.INT) || type.equals(JavaSimpleType.LONG)
                || type.equals(JavaSimpleType.SHORT) || type.equals(JavaSimpleType.BYTE)) {

            if (bits >= 64) {
                return true;
            }
            long maxNum = 1L << bits;
            long value = valueToLong();
            return value < maxNum && -value <= maxNum;
        } else if (type.equals(JavaSimpleType.VOID)) {
            throw new IllegalArgumentException();
        } else {
            return false;
        }
    }

    // TODO: support other types
    private long valueToLong() {
        if (type.equals(JavaSimpleType.INT)) {
            return (Integer) value;
        } else if (type.equals(JavaSimpleType.LONG)) {
            return (Long) value;
        } else if (type.equals(JavaSimpleType.SHORT)) {
            return (Short) value;
        } else {
            throw new UnsupportedOperationException();
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
        if (type.equals(JavaSimpleType.NULL)) {
            return 0;
        } else if (type.equals(JavaSimpleType.BOOL)) {
            return (Boolean) value ? 1 : 0;
        } else if (type.equals(JavaSimpleType.INT)) {
            return (Integer) value;
        } else if (type.equals(JavaSimpleType.LONG)) {
            long longValue = (Long) value;
            if (longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE) {
                throw new IllegalArgumentException();
            }
            return (int) longValue;
        } else if (type.equals(JavaSimpleType.SHORT)) {
            return (Short) value;
        } else if (type.equals(JavaSimpleType.BYTE)) {
            return (Byte) value;
        } else if (type.equals(JavaSimpleType.VOID)) {
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
