package javatoarm.java;

import java.util.List;
import java.util.Set;

public class JavaFile {
    public final JavaName packageName;
    public final Set<JavaName> imports;
    private final List<JavaClass> classes;

    /*
        RI:
            classes contains only one public class
     */

    public JavaFile(JavaName packageName, Set<JavaName> imports, List<JavaClass> classes) {
        assertOnePublic(classes);

        this.packageName = packageName;
        this.imports = imports;
        this.classes = classes;
    }

    public void assertOnePublic(List<JavaClass> classes) {
        boolean hasPublic = false;
        for (JavaClass c : classes) {
            if (c.isPublic) {
                if (hasPublic) {
                    throw new IllegalArgumentException("There can be at most one public class.");
                } else {
                    hasPublic = true;
                }
            }
        }
    }

}
