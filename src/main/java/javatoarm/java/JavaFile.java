package javatoarm.java;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaFile {
    public final List<String> _package;
    public final Set<List<String>> imports;
    private final List<JavaClass> classes;

    /*
        RI:
            classes contains only one public class
     */

    public JavaFile(List<String> _package, Set<List<String>> imports, List<JavaClass> classes) {
        if (_package.size() == 0) {
            throw new IllegalArgumentException();
        }

        assertOnePublic(classes);

        this._package = _package;
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
