package javatoarm;

import javatoarm.javaast.JavaFile;
import javatoarm.parser.JavaParser;
import javatoarm.token.JavaLexer;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class Tests {

    @Test
    public void test() {
        JavaToARM.main(new String[]{"resources/Test1.java"});
    }

    @Test
    public void test_String_java() throws IOException, JTAException {
        String code = Files.readString(Path.of("resources/String.java"));
        JavaLexer lexer = new JavaLexer(code);
        JavaParser parser = new JavaParser(lexer);
        JavaFile file = parser.toJavaTree();
        System.out.println(file);
    }
}
