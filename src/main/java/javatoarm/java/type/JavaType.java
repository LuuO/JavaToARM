package javatoarm.java.type;

public abstract class JavaType {

    abstract String name();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavaType) {
            JavaType that = (JavaType) obj;
            return this.name().equals(that.name());
        }
        return false;
    }
}
