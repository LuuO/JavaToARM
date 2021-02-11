package javatoarm.assembly;

public class Register {
    public final int index;
    private final InstructionSet isa;

    public Register(int index, InstructionSet isa) {
        this.index = index;
        this.isa = isa;

        if (isa == InstructionSet.ARMv7 && index > 16) {
            throw new IllegalArgumentException();
        } else if (isa == InstructionSet.X86_64) {
            throw new UnsupportedOperationException();
        }
    }

    public boolean hasSpecialPurpose() {
        return switch (isa) {
            case ARMv7 -> index >= 13;
            case X86_64 -> throw new UnsupportedOperationException();
        };
    }

    @Override
    public int hashCode() {
        return index;
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
