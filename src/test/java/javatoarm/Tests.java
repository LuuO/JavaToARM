package javatoarm;

import javatoarm.javaast.JavaFile;
import javatoarm.parser.JavaParser;
import javatoarm.token.JavaLexer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Tests {

    @Test
    public void test1() {
        final PrintStream originalErr = System.err;
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err));
        JavaToARM.main(new String[]{"resources/Test1.java"});
        System.setErr(originalErr);
        if (!err.toString().isEmpty()) {
            Assert.fail(err.toString());
        }
    }

    @Test
    public void test_String_java() throws IOException, JTAException {
        String code = Files.readString(Path.of("resources/String.java"));
        JavaLexer lexer = new JavaLexer(code);
        JavaParser parser = new JavaParser(lexer);
        JavaFile file = parser.toJavaAST();
        System.out.println(file);
    }
}
