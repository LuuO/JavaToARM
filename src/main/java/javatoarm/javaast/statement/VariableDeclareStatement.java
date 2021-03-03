package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaAnnotation;
import javatoarm.javaast.JavaClassMember;
import javatoarm.javaast.JavaProperty;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.type.JavaType;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.LocalVariable;
import javatoarm.staticanalysis.Variable;

import java.util.List;
import java.util.Set;

/**
 * Represents a variable declaration
 * <p>
 * Examples: int a = 3, boolean f, Object g
 * </p>
 */
public class VariableDeclareStatement implements JavaClassMember, JavaStatement {
    private final JavaType type;
    private final String name;
    private final JavaRightValue initialValue;
    private final Set<JavaProperty> properties;
    private final List<JavaAnnotation> annotations;

    /**
     * Initialize a variable declaration
     *
     * @param type         type of the variable
     * @param name         name of the variable
     * @param initialValue initial value of the variable
     * @param properties   properties of the variable
     * @param annotations  annotations
     */
    public VariableDeclareStatement(JavaType type, String name, JavaRightValue initialValue,
                                    Set<JavaProperty> properties, List<JavaAnnotation> annotations) {
        this.type = type;
        this.name = name;
        this.initialValue = initialValue;
        this.properties = properties != null ? Set.copyOf(properties) : null; // TODO: validate properties
        this.annotations = annotations != null ? List.copyOf(annotations) : null;
    }

    /**
     * Initialize a variable declaration
     *
     * @param type         type of the variable
     * @param name         name of the variable
     * @param initialValue initial value of the variable
     * @param properties   properties of the variable
     */
    public VariableDeclareStatement(JavaType type, String name, JavaRightValue initialValue,
                                    Set<JavaProperty> properties) {
        this(type, name, initialValue, properties, null);
    }

    /**
     * Check if the variable has a initial value
     *
     * @return true if the variable has a initial value, false otherwise.
     */
    public boolean hasInitialValue() {
        return initialValue != null;
    }

    /**
     * Get the type of the variable
     *
     * @return the type of the variable
     */
    public JavaType type() {
        return type;
    }

    /**
     * Get the name of the variable
     *
     * @return the name of the variable
     */
    public String name() {
        return name;
    }

    /**
     * Get the annotations
     *
     * @return annotations, unmodifiable
     */
    public List<JavaAnnotation> getAnnotations() {
        return annotations;
    }

    /**
     * Get the properties
     *
     * @return properties, unmodifiable
     */
    public Set<JavaProperty> getProperties() {
        return properties;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        LocalVariable variable = parent.declareVariable(type, name);
        if (initialValue != null) {
            if (initialValue instanceof JavaExpression) {
                Variable initial =
                        ((JavaExpression) initialValue).compileExpression(subroutine, parent);
                subroutine.addAssignment(variable, initial);
            } else {
                throw new JTAException.NotImplemented(initialValue.toString());
            }
        }
    }
}
