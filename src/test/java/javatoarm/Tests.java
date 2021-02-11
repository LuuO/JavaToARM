package javatoarm;

import javatoarm.arm.ARMCompiler;
import javatoarm.assembly.Compiler;
import javatoarm.assembly.InstructionSet;
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
        JavaFile file = parser.toJavaTree();
        Compiler compiler = new ARMCompiler();
        file.compileTo(compiler, InstructionSet.ARMv7);
        System.out.println(compiler.toCompleteProgram("FIBB", 5000));
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
