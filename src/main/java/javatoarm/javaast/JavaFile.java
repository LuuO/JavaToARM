package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.javaast.expression.JavaMember;

import java.util.List;
import java.util.Set;

/**
 * Represents a Java file
 */
public class JavaFile {
    public final JavaMember packagePath;
    public final Set<Import> imports;

    private final List<JavaClass> classes;

    /*
        Representation invariants:
            classes contains only one public class
     */

    /**
     * Constructs an instance to represent a Java file
     *
     * @param packagePath the package that it belongs to
     * @param imports     packages to import
     * @param classes     classes in this file
     * @throws JTAException if an error occurs
     */
    public JavaFile(JavaMember packagePath, Set<Import> imports, List<JavaClass> classes) throws JTAException {
        assertOnePublic(classes);

        this.packagePath = packagePath;
        this.imports = imports;
        this.classes = classes;
    }

    /**
     * Compile this Java file
     *
     * @param compiler the compiler object
     * @throws JTAException if an error occurs
     */
    public void compileTo(Compiler compiler) throws JTAException {
        JavaClass firstClass = classes.get(0);
        for (int i = 1; i < classes.size(); i++) {
            if (classes.get(i).isPublic()) {
                firstClass = classes.remove(i);
                break;
            }
        }

        compiler.markGlobalLabel("class_" + firstClass.name);
        firstClass.compileTo(compiler);
        for (JavaClass c : classes) {
            c.compileTo(compiler);
        }
    }

    /**
     * Ensure there is only one public class
     *
     * @param classes the list of classes
     * @throws JTAException if there is more than one public classes
     */
    private void assertOnePublic(List<JavaClass> classes) throws JTAException {
        boolean hasPublic = false;
        for (JavaClass c : classes) {
            if (c.isPublic()) {
                if (hasPublic) {
                    throw new JTAException("There can be at most one public class.");
                } else {
                    hasPublic = true;
                }
            }
        }
    }

    /**
     * Represents a package to import
     */
    public static class Import {
        public final JavaMember path;
        public final boolean isStatic;

        /**
         * Constructs an instance of Import
         *
         * @param member   the member to import
         * @param isStatic is static import
         */
        public Import(JavaMember member, boolean isStatic) {
            this.path = member;
            this.isStatic = isStatic;
        }
    }
}
