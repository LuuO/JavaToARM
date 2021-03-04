package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.javaast.statement.VariableDeclareStatement;
import javatoarm.javaast.type.JavaType;
import javatoarm.staticanalysis.JavaScope;

import java.util.*;

/**
 * Represents a Java class
 */
public class JavaClass implements JavaClassMember {
    public final String name;

    private final JavaType superClass;
    private final Set<JavaType> superInterface;
    private final Set<JavaProperty> properties;
    private final List<Initializer> initializers;

    private final List<VariableDeclareStatement> fields;
    private final LinkedList<JavaFunction> functions;
    private final List<JavaClass> subclasses;
    private final Map<JavaFunction.Signature, JavaType> functionInterfaces;

    /**
     * Constructs a instance to represent a Java class
     *
     * @param name           name of the class
     * @param properties     properties of the class
     * @param superClass     the class that the class extends
     * @param superInterface interfaces that the class implements
     * @param members        members of the class
     * @throws JTAException if an error occurs
     */
    public JavaClass(String name, Set<JavaProperty> properties,
                     JavaType superClass, Set<JavaType> superInterface,
                     List<JavaClassMember> members) throws JTAException {

        this.properties = properties;
        this.name = name;
        this.superClass = superClass;
        this.superInterface = Set.copyOf(superInterface);

        this.initializers = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.functions = new LinkedList<>();
        this.subclasses = new ArrayList<>();
        this.functionInterfaces = new HashMap<>();

        for (JavaClassMember m : members) {
            if (m instanceof VariableDeclareStatement) {
                fields.add((VariableDeclareStatement) m);
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
                throw new JTAException.NotImplemented(m.toString());
            }
        }

        for (JavaFunction function : functions) {
            JavaFunction.Signature functionSignature = function.getSignature();
            JavaType returnType = function.returnType();
            if (functionInterfaces.put(functionSignature, returnType) != null) {
                throw new JTAException.FunctionAlreadyDeclared(functionSignature.name);
            }
        }
    }

    /**
     * Get the return type the function with the provide signatures
     *
     * @param functionSignature the signatures of the function
     * @return the return type of the function
     * @throws JTAException if an error occurs
     */
    public JavaType getFunctionReturnType(JavaFunction.Signature functionSignature) throws JTAException {

        for (Map.Entry<JavaFunction.Signature, JavaType> entry : functionInterfaces.entrySet()) {
            JavaFunction.Signature key = entry.getKey();

            if (key.name.equals(functionSignature.name) &&
                    key.arguments.size() == functionSignature.arguments.size()) {
                boolean match = true;
                for (int i = 0; i < key.arguments.size(); i++) {
                    if (!(key.arguments.get(i).compatibleTo(functionSignature.arguments.get(i)))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return entry.getValue();
                }
            }
        }

        throw new JTAException.UnknownFunction(functionSignature.name);
    }

    /**
     * Compiles this class
     *
     * @param compiler the compiler object
     * @throws JTAException if an error occurs
     */
    public void compileTo(Compiler compiler) throws JTAException {
        JavaScope scope = JavaScope.newClassScope(this);

        compiler.addLabel("class_" + name);
        if (fields.size() != 0) {
            throw new JTAException.NotImplemented("class fields not supported yet");
        }
        for (JavaFunction f : functions) {
            if (f.isPublic) {
                compiler.addJumpLabel("function_" + f.name);
            }
        }
        for (JavaFunction f : functions) {
            f.compileTo(compiler, scope);
        }
    }

    /**
     * Get if this class is public
     *
     * @return if this class is public
     */
    public boolean isPublic() {
        return properties.contains(JavaProperty.PUBLIC);
    }

    /**
     * Get the super class
     *
     * @return the super class
     */
    public JavaType getSuperClass() {
        return superClass;
    }

    /**
     * Get interfaces that this class implements
     *
     * @return the interfaces, unmodifiable
     */
    public Set<JavaType> getSuperInterface() {
        return superInterface;
    }

    /**
     * Get class initializers
     *
     * @return class initializers, unmodifiable
     */
    public List<Initializer> getInitializer() {
        return Collections.unmodifiableList(initializers);
    }

    /**
     * Get subclasses
     *
     * @return subclasses, unmodifiable
     */
    public List<JavaClass> getSubclasses() {
        return Collections.unmodifiableList(subclasses);
    }

    /**
     * Represents class initializers
     */
    public static class Initializer implements JavaClassMember {
        public final boolean isStatic;
        public final JavaBlock block;

        /**
         * Constructs a new instance of class initializer
         *
         * @param block    the block in the initializer
         * @param isStatic is static
         */
        public Initializer(JavaBlock block, boolean isStatic) {
            this.isStatic = isStatic;
            this.block = block;
        }
    }
}
