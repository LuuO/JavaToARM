package javatoarm.java.type;

public class JavaArrayType extends JavaType {

    public final JavaType elementType;

    public JavaArrayType(JavaType elementType) {
        this.elementType = elementType;
    }

    @Override
    String name() {
        return elementType.name() + "[]";
    }
}
