package javatoarm;

import javatoarm.lexer.JavaLexer;
import javatoarm.lexer.JavaLexerException;
import javatoarm.lexer.JavaLexerToken;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Tests {

    @Test
    public void test() throws IOException, JavaLexerException {
        assertTrue(true);
        String code = Files.readString(Path.of("resources/java.txt"));
        JavaLexer lexer = new JavaLexer(code);
        List<JavaLexerToken> tokens = new ArrayList<>();
        while (lexer.hasNext()) {
            tokens.add(lexer.next());
        }
        System.out.println(tokens.size());
    }
}
