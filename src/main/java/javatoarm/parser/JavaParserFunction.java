package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaBlock;
import javatoarm.java.JavaFunction;
import javatoarm.java.JavaProperty;
import javatoarm.java.JavaType;
import javatoarm.java.JavaVariableDeclare;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JavaParserFunction {

    public static JavaFunction parse(JavaLexer lexer) throws JTAException {
        Set<JavaProperty> properties =
            JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS_MEMBER);
        JavaType returnType = JavaParser.parseType(lexer, true);
        String methodName = JavaParser.parseName(lexer);
        List<JavaVariableDeclare> arguments = parseArgumentDeclares(lexer);

        lexer.next(BracketToken.CURLY_L);
        JavaBlock body = (JavaBlock) JavaParserCode.parse(lexer);
        lexer.next(BracketToken.CURLY_R);

        return new JavaFunction(properties, returnType, methodName, arguments, body);
    }

    private static List<JavaVariableDeclare> parseArgumentDeclares(JavaLexer lexer) throws JTAException {
        SplitterToken comma = new SplitterToken(',');
        List<JavaVariableDeclare> arguments = new ArrayList<>();

        lexer.next(BracketToken.ROUND_L);
        if (!lexer.peek().equals(BracketToken.ROUND_R)) {
            for (; ; ) {
                JavaType type = JavaParser.parseType(lexer, true);
                String name = JavaParser.parseName(lexer);
                arguments.add(new JavaVariableDeclare(
                    Collections.emptySet(), type, name, null));

                Token next = lexer.next();
                if (next.equals(BracketToken.ROUND_R)) {
                    break;
                } else if (!next.equals(comma)) {
                    throw new JTAException.UnexpectedToken("',' or ')'", next);
                }
            }
        }

        return arguments;
    }
}
