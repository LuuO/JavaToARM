package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.assembly.Condition;
import javatoarm.java.JavaFile;
import javatoarm.java.JavaProperty;
import javatoarm.java.type.JavaArrayType;
import javatoarm.java.type.JavaParametrizedType;
import javatoarm.java.type.JavaSimpleType;
import javatoarm.java.type.JavaType;
import javatoarm.java.expression.JavaImmediate;
import javatoarm.java.expression.JavaName;
import javatoarm.token.AngleToken;
import javatoarm.token.BracketToken;
import javatoarm.token.ImmediateToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.MemberAccessToken;
import javatoarm.token.NameToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;
import javatoarm.token.operator.Comparison;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaParser {
    private final JavaLexer lexer;

    public JavaParser(JavaLexer lexer) {
        this.lexer = lexer;
    }

    public static void eatSemiColons(JavaLexer lexer) throws JTAException {
        Token semicolon = new SplitterToken(';');
        while (lexer.hasNext() && lexer.peek().equals(semicolon)) {
            lexer.next();
        }
    }

    public static JavaImmediate parseConstant(JavaType type, JavaLexer lexer) throws JTAException {
        SplitterToken comma = new SplitterToken(',');

        if (type instanceof JavaSimpleType) {
            Token next = lexer.next(ImmediateToken.class);
            return new JavaImmediate(type, parseValue(type, (ImmediateToken) next));
        } else if (type instanceof JavaArrayType){
            lexer.next(BracketToken.CURLY_L);

            JavaArrayType arrayType = (JavaArrayType) type;
            JavaType elementType = arrayType.elementType;

            List<Object> arrayValue = new ArrayList<>();
            if (!lexer.peek().equals(BracketToken.CURLY_R)) {
                for (Token next = lexer.next(ImmediateToken.class); ;
                     next = lexer.next(ImmediateToken.class)) {

                    arrayValue.add(parseValue(elementType, (ImmediateToken) next));

                    next = lexer.next();
                    if (next.equals(BracketToken.CURLY_R)) {
                        break;
                    } else if (!next.equals(comma)) {
                        throw new JTAException.UnexpectedToken("',' or '}'", next);
                    }
                }
            }
            return new JavaImmediate(arrayType, arrayValue);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static Object parseValue(JavaType type, ImmediateToken immediateToken)
        throws JTAException {
        if (!type.equals(immediateToken.getType())) {
            throw new JTAException.TypeMismatch(type, immediateToken.getType());
        }
        return immediateToken.getValue();
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
            type = JavaSimpleType.get((KeywordToken) token);
            if (type == null) {
                throw new JTAException.UnexpectedToken("data type", token);
            }
        } else if (token instanceof NameToken) {
            lexer.rewind();
            JavaName typeName = parseNamePath(lexer);

            /* check type parameter */
            if (lexer.nextIf(AngleToken.LEFT)) {
                type = new JavaParametrizedType(typeName, parseType(lexer, false));
                lexer.next(AngleToken.RIGHT);
            } else {
                type = JavaSimpleType.get(typeName);
            }
        } else {
            throw new JTAException.UnexpectedToken("data type", token);
        }

        while (checkIsArray && lexer.nextIf(BracketToken.SQUARE_L)) {
            lexer.next(BracketToken.SQUARE_R);
            type = new JavaArrayType(type);
        }
        return type;
    }

    public static String parseSimpleName(JavaLexer lexer) throws JTAException {
        return lexer.next(NameToken.class).toString();
    }

    public static JavaName parseNamePath(JavaLexer lexer) throws JTAException {
        List<String> path = new ArrayList<>();

        while (lexer.hasNext()) {
            Token token = lexer.next();
            if (token instanceof NameToken) {
                path.add(token.toString());
            } else if (token instanceof MemberAccessToken) {
                path.add(parseSimpleName(lexer));
            } else {
                lexer.rewind();
                if (path.size() == 0 || path.get(path.size() - 1).equals(".")) {
                    throw new JTAException.UnexpectedToken("name", token.toString());
                } else {
                    return new JavaName(path);
                }
            }
        }

        throw new JTAException.UnexpectedToken("name or ';'", "EOF");
    }

    public JavaFile toJavaTree() throws JTAException {
        return FileParser.parseFile(lexer);
    }
}
