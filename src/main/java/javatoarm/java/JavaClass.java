package javatoarm.java;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.InstructionSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JavaClass {
    private final Map<String, JavaFunction.Interface> functionInterfaces;
    boolean isPublic; /* package-private or public*/
    String name;
    List<JavaVariableDeclare> fields;
    LinkedList<JavaFunction> functions;

    public JavaClass(boolean isPublic, String name, List<Member> members)
        throws JTAException {

        this.isPublic = isPublic;
        this.name = name;
        this.fields = new ArrayList<>();
        this.functions = new LinkedList<>();
        this.functionInterfaces = new HashMap<>();

        for (Member m : members) {
            if (m instanceof JavaVariableDeclare) {
                fields.add((JavaVariableDeclare) m);
            } else {
                JavaFunction function = (JavaFunction) m;
                if (function.isPublic) {
                    functions.addFirst(function);
                } else {
                    functions.addLast(function);
                }
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

    public interface Member {
        JavaType type();

        String name();
    }
}
