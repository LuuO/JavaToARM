package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.Subroutine;
import javatoarm.java.statement.JavaVariableDeclare;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JavaFunction implements JavaClass.Member {
    public final boolean isPublic;
    public final String startLabel, epilogueLabel;
    private final JavaType returnType;
    private final String name;
    private final List<JavaVariableDeclare> arguments;
    private final JavaBlock body;

    public JavaFunction(Set<JavaProperty> properties, JavaType returnType,
                        String name, List<JavaVariableDeclare> arguments, JavaBlock body)
        throws JTAException {

        this.returnType = returnType;
        this.name = name;
        this.arguments = arguments;
        this.body = body;

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
        return new Interface(name, arguments, returnType);
    }

    @Override
    public JavaType type() {
        return returnType;
    }

    @Override
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
        public final List<JavaVariableDeclare> arguments;
        public final JavaType returnType;

        public Interface(String name, List<JavaVariableDeclare> arguments, JavaType returnType) {
            this.name = name;
            this.arguments = Collections.unmodifiableList(arguments);
            this.returnType = returnType;
        }

        @Override
        public int hashCode() {
            return name.hashCode() * arguments.hashCode() * returnType.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Interface) {
                Interface that = (Interface) obj;
                return that.name.equals(this.name)
                    && that.arguments.equals(this.arguments)
                    && that.returnType.equals(this.returnType);
            }
            return false;
        }
    }
}
