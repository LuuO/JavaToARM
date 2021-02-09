package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaClass;
import javatoarm.java.JavaFile;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileParser {

    public static JavaFile parseFile(JavaLexer lexer) throws JTAException {
        lexer.next(new KeywordToken(KeywordToken.Keyword._package));
        List<String> packagePath = FileParser.getPackagePath(lexer);
        JavaParser.eatSemiColons(lexer);

        Set<List<String>> imports = FileParser.getImports(lexer);
        JavaParser.eatSemiColons(lexer);

        List<JavaClass> classes = new ArrayList<>();
        while (lexer.hasNext()) {
            classes.add(ClassParser.parse(lexer));
            JavaParser.eatSemiColons(lexer);
        }

        return new JavaFile(packagePath, imports, classes);
    }

    public static List<String> getPackagePath(JavaLexer lexer) throws JTAException.UnexpectedToken {
        Token semicolon = new SplitterToken(';');
        List<String> packagePath = new ArrayList<>(7);

        while (lexer.hasNext()) {
            Token token = lexer.next();
            if (semicolon.equals(token)) {
                if (packagePath.size() == 0) {
                    throw new JTAException.UnexpectedToken("package_name", token.toString());
                } else {
                    return packagePath;
                }
            } else if (token.getTokenType().equals(Token.Type.STRING)) {
                packagePath.add(token.toString());
            } else {
                throw new JTAException.UnexpectedToken("package_name or ';'", token.toString());
            }
        }

        throw new JTAException.UnexpectedToken("package_name or ';'", "EOF");
    }

    public static Set<List<String>> getImports(JavaLexer lexer) throws JTAException.UnexpectedToken {
        Token importToken = new KeywordToken(KeywordToken.Keyword._import);
        Set<List<String>> imports = new HashSet<>();

        while (lexer.hasNext()) {
            Token next = lexer.peek();
            if (next.getTokenType().equals(Token.Type.KEYWORD)) {
                if (next.equals(importToken)) {
                    lexer.next();
                    imports.add(getPackagePath(lexer));
                } else {
                    break;
                }
            } else {
                throw new JTAException.UnexpectedToken(
                    "Java_keyword", next.toString());
            }
        }

        return imports;
    }
}
