package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaAnnotation;
import javatoarm.javaast.JavaProperty;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.statement.JavaVariableDeclare;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.CharToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.Token;
import javatoarm.token.operator.AssignmentOperator;

import java.util.List;
import java.util.Set;

public class FieldParser {

    public static JavaVariableDeclare parse(JavaLexer lexer, List<JavaAnnotation> annotations) throws JTAException {
        Set<JavaProperty> properties =
                JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS_MEMBER);
        JavaType type = TypeParser.parseType(lexer, true);
        String name = JavaParser.parseSimpleName(lexer);

        /* Parse initial value */
        JavaRightValue initialValue = null;
        Token token = lexer.next();
        if (token instanceof AssignmentOperator.Simple) {
            if (lexer.peek().equals(KeywordToken._new)) {
                initialValue = RightValueParser.parseNewInit(lexer);
            } else {
                initialValue = ExpressionParser.parse(lexer);
            }
            token = lexer.next();
        }
        if (!token.equals(CharToken.SEMI_COLON)) {
            throw new JTAException.UnexpectedToken("';' or '='", token);
        }

        return new JavaVariableDeclare(annotations, properties, type, name, initialValue);
    }

}
