package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaImmediate;
import javatoarm.java.JavaFile;
import javatoarm.java.JavaProperty;
import javatoarm.java.JavaType;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.StringToken;
import javatoarm.token.Token;
import javatoarm.token.ValueToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaParser {
    private final JavaLexer lexer;

    public JavaParser(JavaLexer lexer) {
        this.lexer = lexer;
    }

    public static void eatSemiColons(JavaLexer lexer) {
        Token semicolon = new SplitterToken(';');
        while (lexer.hasNext() && lexer.peek().equals(semicolon)) {
            lexer.next();
        }
    }

    public JavaFile toJavaTree() throws JTAException {
        return JavaParserFile.parseFile(lexer);
    }

    public static JavaImmediate parseConstant(JavaType type, JavaLexer lexer) throws JTAException {
        BracketToken leftCurly = new BracketToken('{');
        BracketToken rightCurly = new BracketToken('}');
        SplitterToken comma = new SplitterToken(',');

        if (type.elementType == null) {
            Token next = lexer.next(ValueToken.class);
            return new JavaImmediate(type, parseValue(type, (ValueToken) next));
        } else {
            lexer.next(leftCurly);
            JavaType elementType = type.elementType;

            List<Object> objects = new ArrayList<>();
            if (!lexer.peek().equals(rightCurly)) {
                for (Token next = lexer.next(ValueToken.class); ;
                     next = lexer.next(ValueToken.class)) {

                    objects.add(parseValue(elementType, (ValueToken) next));

                    next = lexer.next();
                    if (next.equals(rightCurly)) {
                        break;
                    } else if (!next.equals(comma)) {
                        throw new JTAException.UnexpectedToken("',' or '}'", next);
                    }
                }
            }
            return new JavaImmediate(JavaType.getArrayTypeOf(type), objects);
        }
    }

    public static Object parseValue(JavaType type, ValueToken valueToken) throws JTAException {
        if (!type.equals(valueToken.getType())) {
            throw new JTAException.TypeMismatch(type, valueToken.getType());
        }
        return valueToken.getValue();
    }

    public static Set<JavaProperty> parseProperties(JavaLexer lexer,
                                                    JavaProperty.Validator validator)
        throws JTAException {

        Set<JavaProperty> properties = new HashSet<>();

        for (Token token = lexer.peek(); ; token = lexer.peek()) {
            JavaProperty property = JavaProperty.get(token);
            if (property != null) {
                validator.validate(property);
                properties.add(property);
                lexer.next();
            } else {
                return properties;
            }
        }
    }

    public static JavaType parseType(JavaLexer lexer, boolean checkIsArray) throws JTAException {
        JavaType type;
        Token token = lexer.next();
        if (token instanceof KeywordToken) {
            type = JavaType.get((KeywordToken) token);
            if (type == null) {
                throw new JTAException.UnexpectedToken("data type", token);
            }
        } else if (token instanceof StringToken) {
            type = JavaType.get(token.toString());
        } else {
            throw new JTAException.UnexpectedToken("data type", token);
        }
        if (checkIsArray && lexer.peek().equals(BracketToken.SQUARE_L)) {
            lexer.next();
            lexer.next(BracketToken.SQUARE_R);
            type = JavaType.getArrayTypeOf(type);
        }
        return type;
    }

    public static String parseName(JavaLexer lexer) throws JTAException {
        Token token = lexer.next();
        if (!(token instanceof StringToken)) {
            throw new JTAException.UnexpectedToken("variable name", token);
        }
        return token.toString();
    }
}
