package javatoarm.staticanalysis;

import javatoarm.JTAException;
import javatoarm.assembly.RegisterAssigner;
import javatoarm.java.JavaType;

public interface Variable {
    /**
     * Get an instance of a temporary variable. Always invoke {#deleteIfIsTemp} after it is used.
     *
     * @param registerAssigner the Register Assigner
     * @param type             the type of variable
     */

    /**
     * Get an instance of a temporary variable. Always invoke {#deleteIfIsTemp} after it is used.
     *
     * @param registerAssigner the Register Assigner
     * @param type             the type of variable
     */
    static LocalVariable getGlobal(RegisterAssigner registerAssigner, JavaType type)
        throws JTAException {
        throw new JTAException.Unsupported("global");
        //return new LocalVariable(null, registerAssigner, type, "temp");
    }

    default void delete() {
    }

    /**
     * Delete this variable if it is temporary. Always call this method after the variable is used.
     *
     * @return true if this is a temporary variable, false otherwise.
     */
    default boolean deleteIfIsTemp() {
        return false;
    }

    JavaType getType();
}
