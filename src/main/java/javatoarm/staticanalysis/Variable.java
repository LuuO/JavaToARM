package javatoarm.staticanalysis;

import javatoarm.javaast.type.JavaType;

/**
 * Represents everything that can be used or store to in Java. It could be a variable, an immediate
 * value, a temporary variable within an expression, a function argument, a value in memory,
 * or other relevant things.
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
}
