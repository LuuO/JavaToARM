package javatoarm;

import javatoarm.java.JavaFile;
import javatoarm.parser.JavaParser;
import javatoarm.token.JavaLexer;
import javatoarm.token.Exceptions;
import javatoarm.token.Token;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Tests {

    @Test
    public void test() throws IOException, Exceptions {
        assertTrue(true);
        String code = Files.readString(Path.of("resources/test1.java"));
        JavaLexer lexer = new JavaLexer(code);
        JavaParser parser = new JavaParser(lexer);
        JavaFile file = parser.toTree();
        System.out.println(file);
    }
}
