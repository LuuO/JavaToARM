package javatoarm.java.type;

public class JavaArrayType extends JavaType {

    public final JavaType elementType;

    public JavaArrayType(JavaType elementType) {
        this.elementType = elementType;
    }

    @Override
    public String name() {
        return elementType.name() + "[]";
    }
}