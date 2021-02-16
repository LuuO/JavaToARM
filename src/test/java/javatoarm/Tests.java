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
    public void test() throws IOException, JTAException {
        assertTrue(true);
        String code = Files.readString(Path.of("resources/test1.java"));
        System.out.println(JavaToARM.compileToARMv7(code, "Fibb", null));
    }

    @Test
    public void test_2() throws IOException, JTAException {
        assertTrue(true);
        String code = Files.readString(Path.of("resources/test2.java"));
        JavaLexer lexer = new JavaLexer(code);
        JavaParser parser = new JavaParser(lexer);
        JavaFile file = parser.toJavaTree();
        System.out.println(file);
    }

    @Test
    public void test_string() throws IOException, JTAException {
        assertTrue(true);
        String code = Files.readString(Path.of("resources/String.java"));
        JavaLexer lexer = new JavaLexer(code);
        JavaParser parser = new JavaParser(lexer);
        JavaFile file = parser.toJavaTree();
        System.out.println(file);
    }
}
