package javatoarm.javaast.type;

import javatoarm.JTAException;

/**
 * Represents a type wildcard.
 * <p>
 * Examples: ?, ? extends T
 * </p>
 */
public class TypeWildcard extends JavaType.Impl {
    public final Bound bound;
    public final JavaType parameterType;

    /**
     * Constructs an instance to represent a type wildcard
     *
     * @param bound         relation with the parameter type
     * @param parameterType the parameter type
     */
    public TypeWildcard(Bound bound, JavaType parameterType) {
        this.bound = bound;
        this.parameterType = parameterType;
    }

    @Override
    public String name() {
        if (parameterType == null) {
            return "?";
        } else {
            return "? %s %s".formatted(bound, parameterType);
        }
    }

    @Override
    public int size() throws JTAException {
        throw new UnsupportedOperationException();
    }

    /**
     * Represents types of relation with the parameter type
     */
    public enum Bound {
        EXTEND, UNBOUNDED, SUPER;

        @Override
        public String toString() {
            return switch (this) {
                case EXTEND -> "extend";
                case UNBOUNDED -> "";
                case SUPER -> "super";
            };
        }
    }
}
