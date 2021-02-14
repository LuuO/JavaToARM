package javatoarm.java.type;

public abstract class JavaType {

    abstract public String name();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavaType) {
            JavaType that = (JavaType) obj;
            return this.name().equals(that.name());
        }
        return false;
    }

    @Override
    public String toString() {
        return name();
    }
}
