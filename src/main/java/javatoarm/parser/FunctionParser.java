package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.*;
import javatoarm.javaast.statement.JavaVariableDeclare;
import javatoarm.javaast.type.JavaArrayType;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FunctionParser {

    public static JavaFunction parse(JavaLexer lexer, String className,
                                     List<JavaAnnotation> annotations) throws JTAException {
        Set<JavaProperty> properties =
                JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS_MEMBER);
        List<JavaType> typeParameters = null;
        if (lexer.peek().equals(AngleToken.LEFT)) {
            typeParameters = TypeParser.parseTypeParameters(lexer);
        }
        JavaType returnType = TypeParser.parseType(lexer, true);

        String methodName;
        if (lexer.peek().equals(BracketToken.ROUND_L) && returnType.name().equals(className)) {
            /* constructor */
            methodName = returnType.toString();
        } else {
            /* other functions */
            methodName = JavaParser.parseSimpleName(lexer);
        }

        List<JavaVariableDeclare> arguments = parseArgumentDeclares(lexer);
        List<JavaType> exceptions = new ArrayList<>();

        if (lexer.nextIf(KeywordToken.Keyword._throws)) {
            for (; ; ) {
                exceptions.add(TypeParser.parseType(lexer, false));
                if (lexer.peek().equals(BracketToken.CURLY_L)
                        || lexer.peek().equals(SplitterToken.SEMI_COLON)) {
                    break;
                } else if (!lexer.nextIf(SplitterToken.COMMA)) {
                    throw new JTAException.UnexpectedToken("Throwable", lexer.peek());
                }
            }

        }

        JavaBlock body;
        if (lexer.nextIf(SplitterToken.SEMI_COLON)) {
            body = null;
        } else {
            body = CodeParser.parseBlock(lexer);
        }

        return new JavaFunction(annotations, properties, typeParameters,
                returnType, methodName, arguments, exceptions, body);
    }

    public static List<JavaVariableDeclare> parseArgumentDeclares(JavaLexer lexer)
            throws JTAException {
        List<JavaVariableDeclare> arguments = new ArrayList<>();

        lexer.next(BracketToken.ROUND_L);
        if (!lexer.nextIf(BracketToken.ROUND_R)) {
            for (; ; ) {
                JavaType type = TypeParser.parseType(lexer, true);
                String name = JavaParser.parseSimpleName(lexer);
                Token next = lexer.next();

                // check char value[]
                if (next.equals(BracketToken.SQUARE_L)) {
                    lexer.next(BracketToken.SQUARE_R);
                    next = lexer.next();
                    type = new JavaArrayType(type);
                }

                arguments.add(new JavaVariableDeclare(
                        Collections.emptySet(), type, name, null));
                if (next.equals(BracketToken.ROUND_R)) {
                    break;
                } else if (!next.equals(SplitterToken.COMMA)) {
                    throw new JTAException.UnexpectedToken("',' or ')'", next);
                }
            }
        }

        return arguments;
    }

    /**
     * (arg1, arg2)
     *
     * @param lexer
     * @return
     * @throws JTAException
     */
    public static List<JavaRightValue> parseCallArguments(JavaLexer lexer)
            throws JTAException {
        List<JavaRightValue> arguments = new ArrayList<>();
        lexer.next(BracketToken.ROUND_L);
        if (!lexer.peek().equals(BracketToken.ROUND_R)) {
            for (; ; ) {
                arguments.add(LambdaParser.parseExpressionOrLambda(lexer));
                Token next = lexer.peek();
                if (next.equals(BracketToken.ROUND_R)) {
                    break;
                } else if (next.equals(SplitterToken.COMMA)) {
                    lexer.next();
                } else {
                    throw new JTAException.UnexpectedToken("',' or ')'", next);
                }
            }
        }
        lexer.next(BracketToken.ROUND_R);
        return arguments;
    }
}
