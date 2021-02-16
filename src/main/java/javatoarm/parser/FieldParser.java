package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaAnnotation;
import javatoarm.javaast.JavaProperty;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.statement.JavaVariableDeclare;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;
import javatoarm.token.operator.AssignmentOperator;

import java.util.List;
import java.util.Set;

public class FieldParser {

    public static JavaVariableDeclare parse(JavaLexer lexer, List<JavaAnnotation> annotations) throws JTAException {
        Set<JavaProperty> properties =
                JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS_MEMBER);
        JavaType type = JavaParser.parseType(lexer, true);
        String name = JavaParser.parseSimpleName(lexer);

        /* Parse initial value */
        JavaRightValue initialValue = null;
        Token token = lexer.next();
        if (token instanceof AssignmentOperator.Simple) {
            if (lexer.peek().equals(new KeywordToken(KeywordToken.Keyword._new))) {
                initialValue = RightValueParser.parseNewInit(lexer);
            } else {
                initialValue = ExpressionParser.parse(lexer);
            }
            token = lexer.next();
        }
        if (!SplitterToken.isSemiColon(token)) {
            throw new JTAException.UnexpectedToken("';' or '='", token);
        }

        return new JavaVariableDeclare(annotations, properties, type, name, initialValue);
    }

}
