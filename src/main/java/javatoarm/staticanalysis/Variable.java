package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.Register;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.javaast.type.JavaType;

/**
 * Represents everything that can be used or store to in Java. It could be a variable, an immediate
 * value, a temporary variable within an expression, a function argument, a value in memory,
 * or other relevant things.
 * Always invoke {@link Variable#deleteIfIsTemp()} after used. This ensures temporary variables are
 * deleted in time and their associated registers are freed.
 */
public interface Variable {

    /**
     * Release all registers that the variable holds. This variable should no longer be used
     * after this method is invoked.
     */
    void delete();

    /**
     * Delete this variable if it is temporary. Always call this method after the variable is used.
     */
    void deleteIfIsTemp();

    /**
     * Get the type of the variable
     *
     * @return the type of the variable
     */
    JavaType getType();

    /**
     * Get the register associated with the variable, request one if necessary.
     * Note that the returned register does not necessary contains the value of
     * the variable. TODO: improve this
     *
     * @param registerAssigner if the variable currently does not hold a register,
     *                        it will request a register from this register assigner.
     *                        If the variable already holds a register,
     *                        this register assigner will be ignored.
     * @return the register associated with the variable
     * @throws JTAException if an error occurs
     */
    Register getRegister(RegisterAssigner registerAssigner) throws JTAException;
}
