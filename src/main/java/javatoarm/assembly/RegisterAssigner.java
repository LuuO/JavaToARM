package javatoarm.assembly;

import javatoarm.JTAException;

/**
 * A RegisterAssigner manages registers in a subroutine. It allocates free registers to variables upon request.
 * Registers should be freed when their holders goes out of scope.
 */
public class RegisterAssigner {
    private final InstructionSet isa;
    private final Register[] registers;

    /**
     * Construct a new Register Assigner with the specified instruction set
     *
     * @param isa the instruction set
     */
    public RegisterAssigner(InstructionSet isa) {
        int numberOfRegisters = switch (isa) {
            case ARMv7 -> 16;
            case X86_64 -> throw new JTAException.NotImplemented("X86");
        };
        this.isa = isa;
        registers = new Register[numberOfRegisters];
        for (int i = 0; i < numberOfRegisters; i++) {
            registers[i] = new Register(i, isa);
        }
    }

    /**
     * Request a general purpose register
     *
     * @return the register assigned
     * @throws JTAException if an error occurs
     */
    public Register requestRegister() throws JTAException {
        for (Register register : registers) {
            if (register.isFree()) {
                register.assign();
                return register;
            }
        }
        throw new JTAException.OutOfRegister();
    }

    /**
     * Request a register to store function argument,
     * following the calling convention in the instruction set.
     *
     * @return the register assigned
     */
    public Register requestArgumentRegister() {
        if (isa == InstructionSet.ARMv7) {
            for (int i = 0; i < 4; i++) {
                Register register = registers[i];
                if (register.isFree()) {
                    register.assign();
                    return register;
                }
            }
            throw new JTAException.NotImplemented("too many arguments");
        } else {
            throw new JTAException.NotImplemented("x86");
        }
    }
}
