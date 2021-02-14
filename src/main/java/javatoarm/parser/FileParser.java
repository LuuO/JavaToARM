package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaClass;
import javatoarm.java.JavaFile;
import javatoarm.java.expression.JavaName;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileParser {

    public static JavaFile parseFile(JavaLexer lexer) throws JTAException {
        lexer.next(new KeywordToken(KeywordToken.Keyword._package));
        JavaName packageName = JavaParser.parseNamePath(lexer);
        JavaParser.eatSemiColons(lexer);

        Set<JavaFile.Import> imports = getImports(lexer);
        JavaParser.eatSemiColons(lexer);

        List<JavaClass> classes = new ArrayList<>();
        while (lexer.hasNext()) {
            classes.add(ClassParser.parse(lexer));
            JavaParser.eatSemiColons(lexer);
        }

        return new JavaFile(packageName, imports, classes);
    }

    public static Set<JavaFile.Import> getImports(JavaLexer lexer) throws JTAException {
        Set<JavaFile.Import> imports = new HashSet<>();

        while (lexer.hasNext()) {
            if (lexer.nextIf(KeywordToken.Keyword._import)) {
                boolean isStatic = false;
                if (lexer.nextIf(KeywordToken.Keyword._static)) {
                    isStatic = true;
                }
                imports.add(new JavaFile.Import(JavaParser.parseNamePath(lexer), isStatic));
                JavaParser.eatSemiColons(lexer);
            } else {
                break;
            }
        }

        return imports;
    }

}
