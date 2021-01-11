package javatoarm.parser;

import javatoarm.java.JavaClass;
import javatoarm.java.JavaFile;
import javatoarm.token.Exceptions;
import javatoarm.token.KeywordToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.SplitterToken;
import javatoarm.token.StringToken;
import javatoarm.token.Token;
import javatoarm.token.operator.PlusMinus;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class JavaParser {
    private final JavaLexer lexer;

    public JavaParser(JavaLexer lexer) {
        this.lexer = lexer;
    }

    public JavaFile toTree() throws Exceptions {
        JavaFile javaFile = createJavaFile();
        eatSemiColons();
        javaFile.imports.addAll(getImports());
        eatSemiColons();
        while (lexer.hasNext()) {
            getJavaClass();
            eatSemiColons();
        }

        return javaFile;
    }

    private void eatSemiColons() {
        Token semicolon = new SplitterToken(';');
        while (lexer.hasNext() && lexer.peek().equals(semicolon)) {
            lexer.next();
        }
    }

    private JavaFile createJavaFile() throws Exceptions.UnexpectedToken {
        Token semicolon = new SplitterToken(';');
        Stack<StringToken> savedTokens = new Stack<>();
        lexer.next(new KeywordToken(KeywordToken.Keyword._package));

        while (lexer.hasNext()) {
            Token token = lexer.next();
            if (semicolon.equals(token)) {
                if (savedTokens.size() == 0) {
                    throw new Exceptions.UnexpectedToken("package_name", token.toString());
                } else {
                    return new JavaFile(String.join("", savedTokens));
                }
            } else if (token.getTokenType().equals(Token.Type.STRING)) {
                savedTokens.push((StringToken) token);
            } else {
                throw new Exceptions.UnexpectedToken("package_name", token.toString());
            }
        }

        throw new Exceptions.UnexpectedToken(semicolon.toString(), "EOF");
    }

    private Set<String> getImports() throws Exceptions.UnexpectedToken {
        Token importToken = new KeywordToken(KeywordToken.Keyword._import);
        Token semicolon = new SplitterToken(';');
        Set<String> imports = new HashSet<>();
        Stack<StringToken> savedTokens = new Stack<>();
        boolean isInImport = false;

        while (lexer.hasNext()) {
            Token token = lexer.peek();
            Token.Type tokenType = token.getTokenType();
            if (isInImport) {
                switch (tokenType) {
                    case STRING -> savedTokens.push((StringToken) lexer.next());
                    case SPLITTER -> {
                        lexer.next(semicolon);
                        if (savedTokens.size() != 0) {
                            imports.add(String.join("", savedTokens));
                            savedTokens.clear();
                            isInImport = false;
                        } else {
                            throw new Exceptions.UnexpectedToken(
                                "package_name", token.toString());
                        }
                    }
                    default -> throw new Exceptions.UnexpectedToken(
                        "package_name or ';'", token.toString());
                }
            } else {
                if (tokenType.equals(Token.Type.KEYWORD)) {
                    if (token.equals(importToken)) {
                        lexer.next();
                        isInImport = true;
                    } else {
                        break;
                    }
                } else {
                    throw new Exceptions.UnexpectedToken(
                        "package_name or ';'", token.toString());
                }
            }
        }

        if (savedTokens.size() != 0) {
            throw new Exceptions.UnexpectedToken("';'", "EOF");
        }
        if (isInImport) {
            throw new Exceptions.UnexpectedToken("package_name or ';'", "EOF");
        }

        return imports;
    }

    private JavaClass getJavaClass() throws Exceptions.UnexpectedToken {
        Token classToken = new KeywordToken(KeywordToken.Keyword._class);
        Token publicToken = new KeywordToken(KeywordToken.Keyword._public);
        Token packageToken = new KeywordToken(KeywordToken.Keyword._package);

        Token nextToken = lexer.next();
        JavaClass javaClass;

        if (nextToken.equals(classToken)) {
            javaClass = new JavaClass(false);
        } else if (nextToken.equals(publicToken)) {
            lexer.next(classToken);
            javaClass = new JavaClass(true);
        } else if (nextToken.equals(packageToken)) {
            Token minusToken = new PlusMinus(false);
            Token privateToken = new KeywordToken(KeywordToken.Keyword._private);
            lexer.next(minusToken);
            lexer.next(privateToken);
            javaClass = new JavaClass(false);
        } else {
            throw new Exceptions.UnexpectedToken(
                "class, public, or package-private", nextToken.toString());
        }
        // names, codes...
        return javaClass;
    }
}
