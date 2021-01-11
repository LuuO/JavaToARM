package javatoarm;

import javatoarm.java.JavaFile;
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
        JavaLexer lexer = new JavaLexer(code);
        JavaParser parser = new JavaParser(lexer);
        JavaFile file = parser.toTree();
        System.out.println(file);
    }
}
