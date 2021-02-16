package javatoarm.javaast;

import javatoarm.JTAException;
import javatoarm.assembly.Compiler;
import javatoarm.javaast.expression.JavaName;

import java.util.List;
import java.util.Set;

public class JavaFile {
    public final JavaName packageName;
    public final Set<Import> imports;
    private final List<JavaClass> classes;

    /*
        RI:
            classes contains only one public class
     */

    public JavaFile(JavaName packageName, Set<Import> imports, List<JavaClass> classes) {
        assertOnePublic(classes);

        this.packageName = packageName;
        this.imports = imports;
        this.classes = classes;
    }

    public void assertOnePublic(List<JavaClass> classes) {
        boolean hasPublic = false;
        for (JavaClass c : classes) {
            if (c.isPublic()) {
                if (hasPublic) {
                    throw new IllegalArgumentException("There can be at most one public class.");
                } else {
                    hasPublic = true;
                }
            }
        }
    }

    public void compileTo(Compiler compiler) throws JTAException {
        JavaClass firstClass = classes.get(0);
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).isPublic()) {
                firstClass = classes.remove(i);
                break;
            }
        }

        compiler.markGlobalLabel("class_" + firstClass.name);
        firstClass.compileTo(compiler, compiler.instructionSet());
        for (JavaClass c : classes) {
            c.compileTo(compiler, compiler.instructionSet());
        }
    }

    public static class Import {
        public final JavaName path;
        public final boolean isStatic;

        public Import(JavaName path, boolean isStatic) {
            this.path = path;
            this.isStatic = isStatic;
        }
    }
}
