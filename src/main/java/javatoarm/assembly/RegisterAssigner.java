package javatoarm.assembly;

import javatoarm.JTAException;

/**
 * An object that assigns Registers to Variables. A Register should be requested when creating the variable,
 * and freed when the variable goes out of scope.
 */
public class RegisterAssigner {

    private final InstructionSet isa;
    private final Register[] registers;

    /**
     * Construct a new Register Assigner with the specified Instruction Set
     *
     * @param isa the instruction set
     * @throws JTAException if an error occurs
     */
    public RegisterAssigner(InstructionSet isa) throws JTAException {
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
     * @param variable the holder variable of the register
     * @return the register assigned
     * @throws JTAException if an error occurs
     */
    public Register requestRegister() throws JTAException {
        for (Register register : registers) {
            if (register.isFree()) {
                return register;
            }
        }
        throw new JTAException.OutOfRegister();
    }

//    /**
//     * Release the register. This method should be invoked when the variable holding the register
//     * goes out of scope.
//     *
//     * @param register the holder
//     */
//    public void release(Register register) {
//        holders[register.index] = null;
//    }

    /**
     * Request a register to store function argument,
     * following the calling convention in the instruction set.
     *
     * @param argument the holder argument of the register
     * @return the register assigned
     * @throws JTAException if an error occurs
     */
    public Register requestArgumentRegister() throws JTAException {
        switch (isa) {
            case ARMv7 -> {
                for (int i = 0; i < 4; i++) {
                    Register register = registers[i];
                    if (register.isFree()) {
                        return register;
                    }
                }
                throw new JTAException.Unsupported("too many arguments");
            }
            case X86_64 -> throw new JTAException.NotImplemented("x86");
            default -> throw new IllegalArgumentException();
        }
    }
}
