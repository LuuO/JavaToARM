package javatoarm.javaast.type;

public class JavaTypeWildcard extends JavaType {
    public final Bound bound;
    public final JavaType parameterType;

    public JavaTypeWildcard(Bound bound, JavaType parameterType) {
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
