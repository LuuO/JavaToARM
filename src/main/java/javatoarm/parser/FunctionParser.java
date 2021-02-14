package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaAnnotation;
import javatoarm.java.JavaBlock;
import javatoarm.java.JavaFunction;
import javatoarm.java.JavaProperty;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.statement.JavaVariableDeclare;
import javatoarm.java.type.JavaArrayType;
import javatoarm.java.type.JavaType;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FunctionParser {

    public static JavaFunction parse(JavaLexer lexer, String className,
                                     List<JavaAnnotation> annotations) throws JTAException {
        Set<JavaProperty> properties =
            JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS_MEMBER);
        JavaType returnType = JavaParser.parseType(lexer, true);

        String methodName;
        if (lexer.peek().equals(BracketToken.ROUND_L) && returnType.name().equals(className)) {
            // constructor
            methodName = returnType.toString();
        } else {
            methodName = JavaParser.parseSimpleName(lexer);
        }

        List<JavaVariableDeclare> arguments = parseArgumentDeclares(lexer);
        List<JavaType> exceptions = new ArrayList<>();

        if (lexer.nextIf(KeywordToken.Keyword._throws)) {
            for (; ; ) {
                exceptions.add(JavaParser.parseType(lexer, false));
                if (lexer.peek().equals(BracketToken.CURLY_L)) {
                    break;
                } else if (!lexer.nextIf(SplitterToken.COMMA)) {
                    throw new JTAException.UnexpectedToken("Throwable", lexer.peek());
                }
            }

        }

        JavaBlock body = CodeParser.parseBlock(lexer);

        return new JavaFunction(
            annotations, properties, returnType, methodName, arguments, exceptions, body);
    }

    private static List<JavaVariableDeclare> parseArgumentDeclares(JavaLexer lexer)
        throws JTAException {
        List<JavaVariableDeclare> arguments = new ArrayList<>();

        lexer.next(BracketToken.ROUND_L);
        if (!lexer.nextIf(BracketToken.ROUND_R)) {
            for (; ; ) {
                JavaType type = JavaParser.parseType(lexer, true);
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
    public static List<JavaExpression> parseCallArguments(JavaLexer lexer)
        throws JTAException {
        List<JavaExpression> arguments = new ArrayList<>();
        lexer.next(BracketToken.ROUND_L);
        if (!lexer.peek().equals(BracketToken.ROUND_R)) {
            for (; ; ) {
                arguments.add(ExpressionParser.parse(lexer));
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
