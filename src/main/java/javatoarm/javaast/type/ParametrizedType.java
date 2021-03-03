package javatoarm.javaast.type;

import javatoarm.javaast.expression.JavaMember;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a parametrized type.
 * <p>
 * Examples: ArrayList&lt Integer &gt, Map&lt ? extends Object, Integer &gt,
 * </p>
 */
public class ParametrizedType extends JavaType.Impl {
    public final JavaMember typePath;
    public final List<JavaType> parameters;

    /**
     * Constructs an instance of parametrized type
     *
     * @param typePath   the type
     * @param parameters type parameters
     */
    public ParametrizedType(JavaMember typePath, List<JavaType> parameters) {
        this.typePath = typePath;
        this.parameters = parameters;
    }

    @Override
    public String name() {
        return "%s<%s>".formatted(typePath,
                parameters.stream().map(JavaType::name).collect(Collectors.joining(", ")));
    }
}
