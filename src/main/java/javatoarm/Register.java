package javatoarm;

public class Register {
    public final int index;
    private final ISA isa;

    public Register(int index, ISA isa) {
        this.index = index;
        this.isa = isa;

        if (isa == ISA.ARM && index > 16) {
            throw new IllegalArgumentException();
        } else if (isa == ISA.X86_64) {
            throw new UnsupportedOperationException();
        }
    }

    public boolean hasSpecialPurpose() {
        return switch (isa) {
            case ARM -> index >= 13;
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
            case ARM -> switch (index) {
                case 13 -> "SP";
                case 14 -> "LR";
                case 15 -> "PC";
                default -> "R" + index;
            };
            case X86_64 -> throw new UnsupportedOperationException();
        };
    }
}
