package javatoarm.java.type;

public abstract class JavaType {

    abstract public String name();

    public boolean compatibleTo(Object obj) {
        return equals(obj);
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
