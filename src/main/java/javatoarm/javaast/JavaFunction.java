package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.Subroutine;
import javatoarm.javaast.statement.JavaVariableDeclare;
import javatoarm.javaast.type.JavaType;
import javatoarm.staticanalysis.JavaScope;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaFunction implements JavaClassMember {
    public final boolean isPublic;
    public final String startLabel, epilogueLabel;
    private final JavaType returnType;
    private final String name;
    private final List<JavaVariableDeclare> arguments;
    private final List<JavaAnnotation> annotations;
    private final List<JavaType> typeParameters;
    private final List<JavaType> exceptions;
    private final JavaBlock body;

    public JavaFunction(List<JavaAnnotation> annotations, Set<JavaProperty> properties, List<JavaType> typeParameters,
                        JavaType returnType, String name, List<JavaVariableDeclare> arguments,
                        List<JavaType> exceptions, JavaBlock body) throws JTAException {

        this.returnType = returnType;
        this.typeParameters = typeParameters;
        this.name = name;
        this.arguments = arguments;
        this.body = body;
        this.annotations = annotations;
        this.exceptions = exceptions;

        startLabel = "function_" + name;
        epilogueLabel = startLabel + "_end";

        isPublic = properties.contains(JavaProperty.PUBLIC);
        for (JavaVariableDeclare arg : arguments) {
            if (arg.hasInitialValue()) {
                throw new JTAException.InvalidOperation("Arguments cannot have initial values.");
            }
        }
    }

    public Interface getInterface() {
        return Interface.get(name, arguments);
    }

    public JavaType returnType() {
        return returnType;
    }

    public String name() {
        return name;
    }

    public void compileTo(Compiler compiler, JavaScope classScope) throws JTAException {
        JavaScope scope = JavaScope.newFunctionScope(classScope, this, arguments);
        Subroutine subroutine = compiler.newSubroutine();

        subroutine.addEmptyLine();
        subroutine.addEmptyLine();
        subroutine.addLabel(startLabel);
        subroutine.pushCalleeSave();
        body.compileCode(subroutine, scope);
        subroutine.addEmptyLine();
        subroutine.addLabel("function_" + name + "_end");
        subroutine.addReturn();

        scope.outOfScope();
        compiler.commitSubroutine(subroutine);
    }

    public static class Interface {
        public final String name;
        public final List<JavaType> arguments;

        public Interface(String name, List<JavaType> argumentTypes) {
            this.name = name;
            this.arguments = argumentTypes;
        }

        public static Interface get(String name, List<JavaVariableDeclare> arguments) {
            return new Interface(name, arguments.stream()
                    .map(JavaVariableDeclare::type).collect(Collectors.toList()));
        }

        @Override
        public int hashCode() {
            return name.hashCode() * arguments.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Interface) {
                Interface that = (Interface) obj;
                return that.name.equals(this.name)
                        && that.arguments.equals(this.arguments);
            }
            return false;
        }
    }
}
