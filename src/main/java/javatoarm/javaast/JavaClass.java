package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.InstructionSet;
import javatoarm.javaast.statement.JavaVariableDeclare;
import javatoarm.javaast.type.JavaType;
import javatoarm.staticanalysis.JavaScope;
import javatoarm.staticanalysis.RegisterAssigner;

import java.util.*;

public class JavaClass implements JavaClassMember {
    public final String name;
    public final Set<JavaType> superClass, superInterface;
    private final Map<JavaFunction.Interface, JavaType> functionInterfaces;
    private final Set<JavaProperty> properties; /* package-private or public*/
    private final List<JavaVariableDeclare> fields;
    private final LinkedList<JavaFunction> functions;
    private final List<JavaClass> subclasses;
    private final List<Initializer> initializers;

    public JavaClass(Set<JavaProperty> properties, String name,
                     Set<JavaType> superClass, Set<JavaType> superInterface,
                     List<JavaClassMember> members) throws JTAException {

        this.properties = properties;
        this.name = name;
        this.superClass = superClass;
        this.superInterface = superInterface;
        this.fields = new ArrayList<>();
        this.functions = new LinkedList<>();
        this.subclasses = new ArrayList<>();
        this.functionInterfaces = new HashMap<>();
        this.initializers = new ArrayList<>();

        for (JavaClassMember m : members) {
            if (m instanceof JavaVariableDeclare) {
                fields.add((JavaVariableDeclare) m);
            } else if (m instanceof Initializer) {
                initializers.add((Initializer) m);
            } else if (m instanceof JavaFunction) {
                JavaFunction function = (JavaFunction) m;
                if (function.isPublic) {
                    functions.addFirst(function);
                } else {
                    functions.addLast(function);
                }
            } else if (m instanceof JavaClass) {
                subclasses.add((JavaClass) m);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        for (JavaFunction function : functions) {
            JavaFunction.Interface functionInterface = function.getInterface();
            JavaType returnType = function.returnType();
            if (functionInterfaces.put(functionInterface, returnType) != null) {
                throw new JTAException.FunctionAlreadyDeclared(functionInterface.name);
            }
        }
    }

    public JavaType getFunctionReturnType(JavaFunction.Interface functionInterface) throws JTAException {

        for (Map.Entry<JavaFunction.Interface, JavaType> entry : functionInterfaces.entrySet()) {
            JavaFunction.Interface key = entry.getKey();

            if (key.name.equals(functionInterface.name) &&
                    key.arguments.size() == functionInterface.arguments.size()) {
                boolean match = true;
                for (int i = 0; i < key.arguments.size(); i++) {
                    if (!(key.arguments.get(i).compatibleTo(functionInterface.arguments.get(i)))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return entry.getValue();
                }
            }
        }

        throw new JTAException.UnknownFunction(functionInterface.name);
    }

    public void compileTo(Compiler compiler, InstructionSet is) throws JTAException {
        JavaScope scope = JavaScope.newClassScope(this, new RegisterAssigner(is));

        compiler.addLabel("class_" + name);
        if (fields.size() != 0) {
            throw new JTAException.Unsupported("class fields not supported yet");
        }
        for (JavaFunction f : functions) {
            if (f.isPublic) {
                compiler.addJumpLabel("function_" + f.name());
            }
        }
        for (JavaFunction f : functions) {
            f.compileTo(compiler, scope);
        }
    }

    public boolean isPublic() {
        return properties.contains(JavaProperty.PUBLIC);
    }

    public static class Initializer implements JavaClassMember {
        public final boolean isStatic;
        public final JavaBlock block;

        public Initializer(JavaBlock block, boolean isStatic) {
            this.isStatic = isStatic;
            this.block = block;
        }
    }
}
