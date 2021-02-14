package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.InstructionSet;
import javatoarm.java.statement.JavaVariableDeclare;
import javatoarm.java.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaClass {
    private final Map<String, JavaFunction.Interface> functionInterfaces;
    private final Set<JavaProperty> properties; /* package-private or public*/
    private final List<JavaVariableDeclare> fields;
    private final LinkedList<JavaFunction> functions;
    private final List<Initializer> initializers;

    public final String name;
    public final Set<JavaType> superClass, superInterface;

    public JavaClass(Set<JavaProperty> properties, String name,
                     Set<JavaType> superClass, Set<JavaType> superInterface,
                     List<Member> members) throws JTAException {

        this.properties = properties;
        this.name = name;
        this.superClass = superClass;
        this.superInterface = superInterface;
        this.fields = new ArrayList<>();
        this.functions = new LinkedList<>();
        this.functionInterfaces = new HashMap<>();
        this.initializers = new ArrayList<>();

        for (Member m : members) {
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
            } else {
                throw new UnsupportedOperationException();
            }
        }
        for (JavaFunction function : functions) {
            JavaFunction.Interface functionInterface = function.getInterface();
            if (functionInterfaces.put(functionInterface.name, functionInterface) != null) {
                throw new JTAException.FunctionAlreadyDeclared(functionInterface.name);
            }
        }
    }

    public JavaFunction.Interface getFunctionInterface(String name) throws JTAException {
        JavaFunction.Interface result = functionInterfaces.get(name);
        if (result == null) {
            throw new JTAException.UnknownFunction(name);
        }
        return result;
    }

    public void compileTo(Compiler compiler, InstructionSet is) throws JTAException {
        JavaScope scope = JavaScope.newClassScope(this, is);

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

    public interface Member {
    }

    public static class Initializer implements Member {
        public final boolean isStatic;
        public final JavaBlock block;

        public Initializer(JavaBlock block, boolean isStatic) {
            this.isStatic = isStatic;
            this.block = block;
        }
    }
}
