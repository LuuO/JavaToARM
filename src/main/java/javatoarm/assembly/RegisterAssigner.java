package javatoarm.assembly;

import javatoarm.JTAException;
import javatoarm.staticanalysis.LocalVariable;
import javatoarm.staticanalysis.Variable;

public class RegisterAssigner {

    public static String SP = "sp";

    private final InstructionSet isa;
    private final Register[] registers;
    private final Variable[] holders;

    /*
        AF:
            GENERAL is the holder of registers that never goes out of scope. (e.g. Stack Pointer)
            holders.get(i) stores the variable using Register i.
            variableNames.get(i) stores the name of the variable correspond to Register i.
     */

    public RegisterAssigner(InstructionSet isa) {
        int numberOfRegisters = switch (isa) {
            case ARMv7 -> 16;
            case X86_64 -> throw new UnsupportedOperationException();
        };
        this.isa = isa;
        holders = new Variable[numberOfRegisters];
        registers = new Register[numberOfRegisters];
        for (int i = 0; i < numberOfRegisters; i++) {
            registers[i] = new Register(i, isa);
        }
    }

    public Register request(Variable variable) throws JTAException {
        for (int i = 0; i < holders.length; i++) {
            if (holders[i] == null && !registers[i].hasSpecialPurpose()) {
                holders[i] = variable;
                return registers[i];
            }
        }
        throw new JTAException.OutOfRegister();
    }

    /**
     * Release the register. This method should be invoked when the variable holding the register
     * goes out of scope.
     *
     * @param register the holder
     */
    public void release(Register register) {
        holders[register.index] = null;
    }

    public Register requestArgumentRegister(LocalVariable argument) throws JTAException {
        switch (isa) {
            case ARMv7:
                for (int i = 0; i < 4; i++) {
                    if (holders[i] == null) {
                        holders[i] = argument;
                    }
                    return registers[i];
                }
                throw new JTAException.Unsupported("too many arguments");
            default:
                throw new UnsupportedOperationException();
        }
    }
}
