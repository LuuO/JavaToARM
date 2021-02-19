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

    private static Set<JavaFile.Import> getImports(JavaLexer lexer) throws JTAException {
        Set<JavaFile.Import> imports = new HashSet<>();

        while (lexer.hasNext()) {
            if (lexer.nextIf(KeywordToken._import)) {
                boolean isStatic = false;
                if (lexer.nextIf(KeywordToken._static)) {
                    isStatic = true;
                }
                imports.add(new JavaFile.Import(JavaParser.parseMemberPath(lexer), isStatic));
                JavaParser.eatSemiColons(lexer);
            } else {
                break;
            }
        }

        return imports;
    }

}
