package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.*;
import javatoarm.token.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaParser {
    private final JavaLexer lexer;

    public JavaParser(JavaLexer lexer) {
        this.lexer = lexer;
    }

    public JavaFile toJavaTree() throws JTAException {
        return FileParser.parseFile(lexer);
    }

    public static void eatSemiColons(JavaLexer lexer) {
        Token semicolon = new SplitterToken(';');
        while (lexer.hasNext() && lexer.peek().equals(semicolon)) {
            lexer.next();
        }
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
        } else if (token instanceof NameToken) {
            lexer.rewind();
            type = JavaType.get(parseNamePath(lexer));
        } else {
            throw new JTAException.UnexpectedToken("data type", token);
        }
        if (checkIsArray && lexer.nextIf(BracketToken.SQUARE_L)) {
            lexer.next(BracketToken.SQUARE_R);
            type = JavaType.getArrayTypeOf(type);
        }
        return type;
    }

    public static String parseSimpleName(JavaLexer lexer) throws JTAException {
        return lexer.next(NameToken.class).toString();
    }

    public static JavaName parseNamePath(JavaLexer lexer) throws JTAException {
        List<String> path = new ArrayList<>(7);

        while (lexer.hasNext()) {
            Token token = lexer.next();
            if (token instanceof NameToken) {
                path.add(token.toString());
            } else if (token instanceof MemberAccessToken) {
                path.add(parseSimpleName(lexer));
            } else {
                lexer.rewind();
                if (path.size() == 0) {
                    throw new JTAException.UnexpectedToken("name", token.toString());
                } else {
                    return new JavaName(path);
                }
            }
        }

        throw new JTAException.UnexpectedToken("name or ';'", "EOF");
    }
}
