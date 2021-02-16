package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaAnnotation;
import javatoarm.java.JavaFile;
import javatoarm.java.JavaProperty;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.expression.JavaImmediate;
import javatoarm.java.expression.JavaName;
import javatoarm.java.type.*;
import javatoarm.token.*;
import javatoarm.token.operator.TernaryToken;

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
        } else if (type instanceof JavaArrayType) {
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
                List<JavaType> typeParameters = new ArrayList<>();

                if (!lexer.nextIf(AngleToken.RIGHT)) {
                    do {
                        if (lexer.nextIf(TernaryToken.QUESTION)) {
                            JavaTypeWildcard.Bound bound;
                            JavaType wildcardType;
                            if (lexer.nextIf(KeywordToken.Keyword._extends)) {
                                bound = JavaTypeWildcard.Bound.EXTEND;
                                wildcardType = parseType(lexer, false);
                            } else if (lexer.nextIf(KeywordToken.Keyword._super)) {
                                bound = JavaTypeWildcard.Bound.SUPER;
                                wildcardType = parseType(lexer, false);
                            } else {
                                bound = JavaTypeWildcard.Bound.UNBOUNDED;
                                wildcardType = null;
                            }
                            typeParameters.add(new JavaTypeWildcard(bound, wildcardType));
                        } else {
                            typeParameters.add(parseType(lexer, false));
                        }
                    } while (lexer.nextIf(SplitterToken.COMMA));
                    lexer.next(AngleToken.RIGHT);
                }

                type = new JavaParametrizedType(typeName, typeParameters);
            } else {
                type = JavaSimpleType.get(typeName);
            }
        } else {
            throw new JTAException.UnexpectedToken("data type", token);
        }
        /* check varargs */
        if (lexer.nextIf(MemberAccessToken.INSTANCE)) {
            lexer.next(MemberAccessToken.INSTANCE);
            lexer.next(MemberAccessToken.INSTANCE);
            type = new JavaArrayType(type);
        } else {
            while (checkIsArray && lexer.nextIf(BracketToken.SQUARE_L)) {
                lexer.next(BracketToken.SQUARE_R);
                type = new JavaArrayType(type);
            }
        }
        return type;
    }

    public static String parseSimpleName(JavaLexer lexer) throws JTAException {
        return lexer.next(NameToken.class).toString();
    }

    public static JavaName parseNamePath(JavaLexer lexer) throws JTAException {
        Token token = lexer.next();
        if (token instanceof NameToken || token.equals(KeywordToken.THIS)) {
            List<String> path = new ArrayList<>();
            path.add(token.toString());
            while (lexer.nextIf(MemberAccessToken.INSTANCE)) {
                Token next = lexer.next();
                if (next instanceof NameToken) {
                    path.add(next.toString());
                } else {
                    lexer.rewind(2);
                    break;
                }
            }
            return new JavaName(path);
        } else {
            throw new JTAException.UnexpectedToken("name", token);
        }
    }

    public static List<JavaAnnotation> parseAnnotations(JavaLexer lexer) throws JTAException {
        ArrayList<JavaAnnotation> annotations = new ArrayList<>();
        while (lexer.nextIf(AnnotationToken.INSTANCE)) {
            JavaName name = parseNamePath(lexer);
            if (lexer.nextIf(BracketToken.ROUND_L) && !lexer.nextIf(BracketToken.ROUND_R)) {
                JavaExpression parameter = ExpressionParser.parse(lexer);
                lexer.next(BracketToken.ROUND_R);

                annotations.add(new JavaAnnotation(name, parameter));
            } else {
                annotations.add(new JavaAnnotation(name));
            }
        }
        return annotations;
    }

}
