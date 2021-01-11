package javatoarm.java;

import java.util.HashSet;
import java.util.Set;

public class JavaFile {
    public final String _package;
    public final Set<String> imports;
    private final Set<JavaClass> classes;

    /*
        RI:
            classes contains only one public class
     */

    public JavaFile(String _package) {
        if (_package.length() == 0) {
            throw new IllegalArgumentException();
        }

        this._package = _package;
        this.imports = new HashSet<>();
        this.classes = new HashSet<>();
    }

    public void addClass(JavaClass javaClass) {
        if (javaClass.isPublic) {
            for (JavaClass c : classes) {
                if (c.isPublic)
                    throw new IllegalArgumentException("There can be at most one public class.");
            }
        }
    }

}
