package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaClass;
import javatoarm.javaast.JavaFile;
import javatoarm.javaast.expression.JavaMember;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileParser {

    /**
     * Parse all tokens in the Java file
     *
     * @param lexer the lexer
     * @return a Java AST representing the file
     * @throws JTAException if an error occurs
     */
    public static JavaFile parseFile(JavaLexer lexer) throws JTAException {
        lexer.next(KeywordToken._package);
        JavaMember packagePath = JavaParser.parseMemberPath(lexer);
        JavaParser.eatSemiColons(lexer);

        Set<JavaFile.Import> imports = getImports(lexer);
        JavaParser.eatSemiColons(lexer);

        List<JavaClass> classes = new ArrayList<>();
        while (lexer.hasNext()) {
            classes.add(ClassParser.parse(lexer));
            JavaParser.eatSemiColons(lexer);
        }

        return new JavaFile(packagePath, imports, classes);
    }

    /**
     * Parse imports of the file.
     *
     * @param lexer the lexer
     * @return a set of imports
     * @throws JTAException if an error occurs
     */
    private static Set<JavaFile.Import> getImports(JavaLexer lexer) throws JTAException {
        Set<JavaFile.Import> imports = new HashSet<>();

        while (lexer.nextIf(KeywordToken._import)) {
            boolean isStatic = lexer.nextIf(KeywordToken._static);
            imports.add(new JavaFile.Import(JavaParser.parseMemberPath(lexer), isStatic));
            JavaParser.eatSemiColons(lexer);
        }

        return imports;
    }

}
