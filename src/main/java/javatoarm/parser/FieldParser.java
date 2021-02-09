package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaVariableDeclare;
import javatoarm.java.JavaProperty;
import javatoarm.java.JavaRightValue;
import javatoarm.java.JavaType;
import javatoarm.token.JavaLexer;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;
import javatoarm.token.operator.AssignmentOperator;

import java.util.Set;

public class FieldParser {

    public static JavaVariableDeclare parse(JavaLexer lexer) throws JTAException {
        Set<JavaProperty> properties =
            JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS_MEMBER);
        JavaType type = JavaParser.parseType(lexer, true);
        String name = JavaParser.parseSimpleName(lexer);

        /* Parse initial value */
        JavaRightValue initialValue = null;
        Token token = lexer.next();
        if (token instanceof AssignmentOperator.Simple) {
            initialValue = JavaParser.parseConstant(type, lexer);
            token = lexer.next();
        }
        if (!SplitterToken.isSemiColon(token)) {
            throw new JTAException.UnexpectedToken("';' or '='", token);
        }

        return new JavaVariableDeclare(properties, type, name, initialValue);
    }

}
