package javatoarm.assembly;

import javatoarm.JTAException;
import javatoarm.variable.Variable;

/**
 * Represents a CPU register
 */
public class Register {
    public final int index;
    private final InstructionSet isa;
    private Variable holder;

    /**
     * Create a representation of a register
     *
     * @param index the index
     * @param isa   instruction set
     */
    public Register(int index, InstructionSet isa) {
        this.index = index;
        this.isa = isa;

        if (isa == InstructionSet.ARMv7 && index > 16) {
            throw new IllegalArgumentException();
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

    public void release() {
        if (holder == null) {
            throw new IllegalArgumentException("Register is not assigned");
        }
        holder = null;
    }

    public void assign(Variable variable) {
        if (holder != null) {
            throw new IllegalArgumentException("Register is already assigned");
        }
        holder = variable;
    }

    public boolean isFree() {
        return holder == null;
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
