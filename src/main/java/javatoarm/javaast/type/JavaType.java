package javatoarm.javaast.type;

import javatoarm.JTAException;

/**
 * Represents a Java data type
 */
public interface JavaType {

    /**
     * Get the name of the type
     *
     * @return name of the type
     */
    default String name() {
        return toString();
    }

    /**
     * Check if this type is compatible to the provided type.
     * Two types are compatible to each other if they can be converted implicitly.
     *
     * @param that the other type
     * @return if they are compatible
     */
    default boolean compatibleTo(JavaType that) {
        return equals(that);
    }

    /**
     * Get the size of the type.
     *
     * @return number of bytes that one element of the type will occupy.
     */
    int size();

    /**
     * Default implementation
     */
    abstract class Impl implements JavaType {
        @Override
        public boolean compatibleTo(JavaType that) {
            return equals(that);
        }

        @Override
        public int size() {
            throw new JTAException.NotImplemented("Data type size");
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof JavaType) {
                JavaType that = (JavaType) obj;
                return this.name().equals(that.name());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name().hashCode();
        }

        @Override
        public String toString() {
            return name();
        }
    }
}
