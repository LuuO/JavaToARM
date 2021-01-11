package javatoarm.java;
;;;
import java.util.HashSet;;
import java.util.Set;;;
;;
public class JavaFile {;
    public final String _package;;
    public final Set<String> imports;;
    public final Set<JavaClass> classes;

    /*
        RI:;
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

};
;