package javatoarm.assembly;

import javatoarm.JTAException;

/**
 * Represents a CPU register
 */
public class Register {
    private final int index;
    private final InstructionSet isa;
    private boolean isAssigned;

    /**
     * Create a representation of a register
     *
     * @param index the index
     * @param isa   instruction set
     */
    public Register(int index, InstructionSet isa) {
        this.index = index;
        this.isa = isa;

        if (index < 0 || (isa == InstructionSet.ARMv7 && index > 16)) {
            throw new IllegalArgumentException("Invalid register index for ARMv7: " + index);
        } else if (isa == InstructionSet.X86_64) {
            throw new JTAException.NotImplemented("X86_64");
        }
    }

    /**
     * Check if the register has a special purpose
     *
     * @return true if the register has a special purpose, false otherwise
     */
    @Deprecated
    private boolean hasSpecialPurpose() {
        return switch (isa) {
            case ARMv7 -> index >= 13;
            case X86_64 -> throw new JTAException.NotImplemented("x86");
        };
    }

    /**
     * Release the register. This method should be invoked after the holder variable is deleted.
     */
    public void release() {
        if (!isAssigned) {
            throw new IllegalArgumentException("Register is not assigned");
        }
        isAssigned = false;
    }

    /**
     * Set the status of the register as assigned.
     * This method should be invoked after the register has been assigned to a variable.
     */
    public void assign() {
        if (isAssigned) {
            throw new IllegalArgumentException("Register is already assigned");
        }
        isAssigned = true;
    }

    /**
     * Check if the register is assigned to some variable.
     *
     * @return true if the register is not assigned, false otherwise.
     */
    public boolean isFree() {
        return !isAssigned;
    }

    @Override
    public int hashCode() {
        return 1 << index + 1 << (index / 4);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Register) {
            Register that = (Register) obj;
            return this.index == that.index;
        }
        return false;
    }

    @Override
    public String toString() {
        return switch (isa) {
            case ARMv7 -> switch (index) {
                case 13 -> "SP";
                case 14 -> "LR";
                case 15 -> "PC";
                default -> "R" + index;
            };
            case X86_64 -> throw new UnsupportedOperationException();
        };
    }
}
