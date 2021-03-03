package javatoarm.javaast.type;

/**
 * Represents an array of some types
 * <p>
 * Examples: int[], String[], boolean[][]
 * </p>
 */
public class ArrayType extends JavaType.Impl {
    public final JavaType elementType;

    /**
     * Constructs a new ArrayType
     *
     * @param elementType element type
     */
    public ArrayType(JavaType elementType) {
        this.elementType = elementType;
    }

    @Override
    public String name() {
        return elementType.name() + "[]";
    }
}
