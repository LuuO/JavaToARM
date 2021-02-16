package javatoarm.javaast.statement;

import javatoarm.JTAException;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.JavaAnnotation;
import javatoarm.javaast.JavaClassMember;
import javatoarm.javaast.JavaProperty;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaNewArray;
import javatoarm.javaast.type.JavaType;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.LocalVariable;
import javatoarm.staticanalysis.Variable;

import java.util.List;
import java.util.Set;

public class JavaVariableDeclare implements JavaClassMember, JavaStatement {
    private final JavaType type;
    private final String name;
    private final JavaRightValue initialValue;
    private final Set<JavaProperty> properties;
    private final List<JavaAnnotation> annotations;

    public JavaVariableDeclare(List<JavaAnnotation> annotations, Set<JavaProperty> properties,
                               JavaType type, String name, JavaRightValue initialValue) {
        this.type = type;
        this.name = name;
        this.properties = properties; // TODO: validate properties
        this.initialValue = initialValue;
        this.annotations = annotations;
    }

    public JavaVariableDeclare(Set<JavaProperty> properties,
                               JavaType type, String name, JavaRightValue initialValue) {
        this(null, properties, type, name, initialValue);
    }

    public boolean hasInitialValue() {
        return initialValue != null;
    }

    public boolean hasProperties() {
        return properties.size() != 0;
    }

    public JavaType type() {
        return type;
    }

    public String name() {
        return name;
    }

    @Override
    public void compileCode(Subroutine subroutine, JavaScope parent) throws JTAException {
        LocalVariable variable = parent.declareVariable(type, name);
        if (initialValue != null) {
            if (initialValue instanceof JavaNewArray) {
                JavaExpression sizeExpression =
                        ((JavaNewArray) initialValue).memorySize();
                Variable size = sizeExpression.compileExpression(subroutine, parent);
                subroutine.malloc(size, variable.getRegister());
                size.deleteIfIsTemp();
            } else if (initialValue instanceof JavaExpression) {
                Variable initial =
                        ((JavaExpression) initialValue).compileExpression(subroutine, parent);
                subroutine.addAssignment(variable, initial);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}
