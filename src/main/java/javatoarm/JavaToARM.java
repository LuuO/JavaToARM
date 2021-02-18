package javatoarm;

import javatoarm.assembly.Compiler;
import javatoarm.assembly.InstructionSet;
import javatoarm.javaast.JavaFile;
import javatoarm.parser.JavaParser;
import javatoarm.token.JavaLexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class JavaToARM {
    private static final int MB = 1024 * 1024;

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Invalid arguments");
            System.err.println("Format should be: \"path/to/the_file_to_compile.java\" [Memory Size (in MB)]");
            return;
        }

        /* Resolve memory size */
        int memorySize = args.length >= 2 ? Integer.parseInt(args[1]) : MB;

        /* Resolve source Path */
        Path sourcePath;
        try {
            sourcePath = Path.of(args[0]);
        } catch (InvalidPathException e) {
            System.err.printf("Cannot convert \"%s\" to a Path: \n", args[0]);
            e.printStackTrace();
            return;
        }

        /* Resolve class name */
        String sourceFileName = sourcePath.getFileName().toString();
        if (!sourceFileName.endsWith(".java")) {
            System.err.printf("Error: %s is not a .java file\n", sourcePath);
            return;
        }
        String className = sourceFileName.substring(0, sourceFileName.length() - 5);

        /* Resolve output file name */
        String outputFileName = className + ".s";

        /* Resolve output Path */
        Path outputPath;
        try {
            outputPath = sourcePath.resolveSibling(outputFileName);
        } catch (InvalidPathException e) {
            System.err.printf("Cannot resolve output Path: sibling \"%s\" of %s.\n", outputFileName, sourcePath);
            e.printStackTrace();
            return;
        }

        /* Read source Code */
        String sourceCode;
        try {
            sourceCode = Files.readString(sourcePath);
        } catch (IOException e) {
            System.err.printf("Error reading source file: %s\n", sourcePath);
            e.printStackTrace();
            return;
        }

        /* Compile */
        String assemblyOutput;
        try {
            assemblyOutput = compileToARMv7(sourceCode, className, memorySize);
        } catch (JTAException e) {
            System.err.println("Compiler error: ");
            e.printStackTrace();
            return;
        } catch (RuntimeException e) {
            System.err.println("Program error: ");
            e.printStackTrace();
            return;
        }

        /* Write output instructions */
        try {
            Files.writeString(outputPath, assemblyOutput,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error writing output file: ");
            e.printStackTrace();
            return;
        }

        System.out.println("Compile successful");
        System.out.printf("Instructions were written to \"%s\" \n", outputPath);
    }

    /**
     * Compile the provided java program to ARMv7 instructions with default memory size (2 MB).
     *
     * @param sourceCode source java code
     * @param mainClass  mainClass.main will be the entry point of the program
     * @return output instructions
     * @throws JTAException if an error occurs during compilation
     */
    public static String compileToARMv7(String sourceCode, String mainClass) throws JTAException {
        return compileToARMv7(sourceCode, mainClass, 2 * MB);
    }

    /**
     * Compile the provided java program to ARMv7 instructions.
     *
     * @param sourceCode source java code
     * @param mainClass  mainClass.main will be the entry point of the program
     * @param memorySize the runtime memory size of the program.
     *                   The stack pointer will be initialized with this value.
     * @return output instructions
     * @throws JTAException if an error occurs during compilation
     */
    public static String compileToARMv7(String sourceCode, String mainClass, int memorySize)
            throws JTAException {

        JavaLexer lexer = new JavaLexer(sourceCode);
        JavaParser parser = new JavaParser(lexer);
        JavaFile file = parser.toJavaAST();
        Compiler compiler = Compiler.getCompiler(InstructionSet.ARMv7);
        file.compileTo(compiler);
        return compiler.toCompleteProgram(mainClass, memorySize);
    }

}
