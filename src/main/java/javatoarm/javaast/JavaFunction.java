package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.statement.VariableDeclareStatement;
import javatoarm.javaast.type.JavaType;
import javatoarm.staticanalysis.JavaScope;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a function declaration or definition in Java
 */
public class JavaFunction implements JavaClassMember {
    public final boolean isPublic;
    public final String name;
    public final String startLabel, epilogueLabel;

    private final JavaType returnType;
    private final JavaBlock body;
    private final List<VariableDeclareStatement> arguments;
    private final Set<JavaProperty> properties;
    private final List<JavaAnnotation> annotations;
    private final List<JavaType> typeParameters;
    private final List<JavaType> exceptions;

    /**
     * Construct a function definition
     *
     * @param returnType     return type
     * @param name           name
     * @param body           body
     * @param arguments      arguments
     * @param properties     properties
     * @param typeParameters type parameters
     * @param annotations    annotations
     * @param exceptions     checked exceptions that the function could throw
     * @throws JTAException if an error occurs
     */
    public JavaFunction(JavaType returnType, String name, JavaBlock body, List<VariableDeclareStatement> arguments,
                        Set<JavaProperty> properties, List<JavaType> typeParameters, List<JavaAnnotation> annotations,
                        List<JavaType> exceptions) throws JTAException {

        this.returnType = returnType;
        this.name = name;
        this.body = body;

        this.arguments = List.copyOf(arguments);
        this.properties = Set.copyOf(properties);
        this.annotations = annotations != null ? List.copyOf(annotations) : null;
        this.typeParameters = typeParameters != null ? List.copyOf(typeParameters) : null;
        this.exceptions = exceptions != null ? List.copyOf(exceptions) : null;

        startLabel = "function_" + name;
        epilogueLabel = startLabel + "_end";

        isPublic = properties.contains(JavaProperty.PUBLIC);
        for (VariableDeclareStatement arg : arguments) {
            if (arg.hasInitialValue()) {
                throw new JTAException.InvalidOperation("Arguments cannot have initial values.");
            }
        }
    }

    /**
     * Get the return type of the function
     *
     * @return the return type
     */
    public JavaType returnType() {
        return returnType;
    }

    /**
     * Get the properties of the function
     *
     * @return the properties
     */
    public Set<JavaProperty> getProperties() {
        return properties;
    }

    /**
     * Get the annotations of the function
     *
     * @return the annotations
     */
    public List<JavaAnnotation> getAnnotations() {
        return annotations;
    }

    /**
     * Get the type parameters of the function
     *
     * @return the type parameters
     */
    public List<JavaType> getTypeParameters() {
        return typeParameters;
    }

    /**
     * Get the checked exceptions that this function might throw
     *
     * @return types of the checked exceptions
     */
    public List<JavaType> getExceptions() {
        return exceptions;
    }

    /**
     * Get the signatures of the function
     *
     * @return an object that represent the function's signatures
     */
    public Signature getSignature() {
        return new Signature(name, arguments.stream()
                .map(VariableDeclareStatement::type).collect(Collectors.toList()));
    }

    /**
     * Compile this function
     *
     * @param compiler   the compiler object
     * @param classScope the scope of the class that this function belongs to
     * @throws JTAException if an error occurs
     */
    public void compileTo(Compiler compiler, JavaScope classScope) throws JTAException {
        JavaScope scope = JavaScope.newFunctionScope(classScope, this, arguments);
        Subroutine subroutine = compiler.newSubroutine();

        subroutine.addEmptyLine();
        subroutine.addEmptyLine();
        subroutine.addLabel(startLabel);
        subroutine.pushCalleeSave();
        if (body == null) {
            throw new JTAException.NotImplemented("function null body");
        }
        body.compileCode(subroutine, scope);
        subroutine.addEmptyLine();
        subroutine.addLabel("function_" + name + "_end");
        subroutine.addReturn();

        scope.outOfScope();
        compiler.commitSubroutine(subroutine);
    }

    /**
     * Represents the signatures of a function
     */
    public static class Signature {
        public final String name;
        public final List<JavaType> arguments;

        /**
         * Construct an instance to represent the signatures of a function
         *
         * @param name          the name of the function
         * @param argumentTypes the data types of the arguments of the function
         */
        public Signature(String name, List<JavaType> argumentTypes) {
            this.name = name;
            this.arguments = argumentTypes;
        }

        @Override
        public int hashCode() {
            return name.hashCode() * arguments.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Signature) {
                Signature that = (Signature) obj;
                return that.name.equals(this.name)
                        && that.arguments.equals(this.arguments);
            }
            return false;
        }
    }
}
