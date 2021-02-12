package javatoarm;

import javatoarm.assembly.Compiler;
import javatoarm.assembly.InstructionSet;
import javatoarm.java.JavaFile;
import javatoarm.parser.JavaParser;
import javatoarm.token.JavaLexer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaToARM {
    private static final int MB = 1024 * 1024;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Error: missing path to source file");
            return;
        }
        if (args.length == 1) {
            System.err.println("Error: missing the name of the main class");
            return;
        }

        System.out.println(args[0]);

        System.out.println(Files.readString(Path.of(args[0]), StandardCharsets.ISO_8859_1));


        try {
            String code = Files.readString(Path.of("resources/test1.java"));
            compileToARMv7(code, args[1], MB);
        } catch (JTAException exception) {

        }
    }

    public static String compileToARMv7(String sourceCode, String mainClass, Integer stackPosition)
        throws JTAException {
        if (stackPosition == null) {
            stackPosition = MB;
        }

        JavaLexer lexer = new JavaLexer(sourceCode);
        JavaParser parser = new JavaParser(lexer);
        JavaFile file = parser.toJavaTree();
        Compiler compiler = Compiler.getCompiler(InstructionSet.ARMv7);
        file.compileTo(compiler);
        return compiler.toCompleteProgram(mainClass, stackPosition);
    }

}
