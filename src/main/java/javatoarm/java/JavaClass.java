package javatoarm.java;

import java.util.ArrayList;
import java.util.List;

public class JavaClass {
    boolean isPublic; /* package-private or public*/
    String name;
    List<JavaVariableDeclare> fields;
    List<JavaFunction> functions;

    public JavaClass(boolean isPublic, String name, List<Member> members) {
        this.isPublic = isPublic;
        this.name = name;
        this.fields = new ArrayList<>();
        this.functions = new ArrayList<>();

        for (Member m : members) {
            if (m instanceof JavaVariableDeclare) {
                fields.add((JavaVariableDeclare) m);
            } else {
                functions.add((JavaFunction) m);
            }
        }
    }

    public interface Member {
        JavaType type();
        String name();
    }
}
