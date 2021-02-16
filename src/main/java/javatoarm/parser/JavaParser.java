package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaAnnotation;
import javatoarm.javaast.JavaFile;
import javatoarm.javaast.JavaProperty;
import javatoarm.javaast.expression.ImmediateExpression;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaName;
import javatoarm.javaast.type.JavaArrayType;
import javatoarm.javaast.type.JavaSimpleType;
import javatoarm.javaast.type.JavaType;
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

    public static void eatSemiColons(JavaLexer lexer) throws JTAException {
        //noinspection StatementWithEmptyBody
        while (lexer.hasNext() && lexer.nextIf(SplitterToken.SEMI_COLON)) ;
    }

    public static ImmediateExpression parseConstant(JavaType type, JavaLexer lexer) throws JTAException {

        if (type instanceof JavaSimpleType) {
            Token next = lexer.next(ImmediateToken.class);
            return new ImmediateExpression(type, parseValue(type, (ImmediateToken) next));
        } else if (type instanceof JavaArrayType) {
            lexer.next(BracketToken.CURLY_L);

            JavaArrayType arrayType = (JavaArrayType) type;
            JavaType elementType = arrayType.elementType;

            List<Object> arrayValue = new ArrayList<>();
            if (!lexer.peek().equals(BracketToken.CURLY_R)) {
                for (Token next = lexer.next(ImmediateToken.class); ;
                     next = lexer.next(ImmediateToken.class)) {

                    arrayValue.add(parseValue(elementType, (ImmediateToken) next));

                    if (lexer.nextIf(BracketToken.CURLY_R)) {
                        break;
                    } else if (!lexer.nextIf(SplitterToken.SEMI_COLON)) {
                        throw new JTAException.UnexpectedToken("',' or '}'", next);
                    }
                }
            }
            return new ImmediateExpression(arrayType, arrayValue);
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

    public JavaFile toJavaTree() throws JTAException {
        return FileParser.parseFile(lexer);
    }

}
