package javatoarm.javaast.type;

import javatoarm.javaast.expression.JavaMember;

/**
 * Represents a user-defined type
 * Examples: String, ArrayList
 */
public class UserDefinedType extends JavaType.Impl {
    public static UserDefinedType STRING = new UserDefinedType("String");

    public final String name;

    private UserDefinedType(String name) {
        this.name = name;
    }

    /**
     * Get a user-defined type
     *
     * @param typePath path to the type
     * @return the user-defined type
     */
    public static UserDefinedType get(JavaMember typePath) {
        String name = typePath.toString();
        if (name.equals("String")) {
            return STRING;
        }
        return new UserDefinedType(name);
    }

    @Override
    public String name() {
        return name;
    }
}
