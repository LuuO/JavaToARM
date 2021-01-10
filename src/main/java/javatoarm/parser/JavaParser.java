package javatoarm.parser;

import javatoarm.lexer.JavaLexer;

public class JavaParser {
    private final JavaLexer lexer;

    public JavaParser(JavaLexer lexer) {
        this.lexer = lexer;
    }

    public JavaParserClass toClassTree() {
        return null;
    }
}
