package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaClass;
import javatoarm.java.JavaProperty;
import javatoarm.java.type.JavaType;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;
import javatoarm.token.operator.AssignmentOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Methods for parsing a Java Class
 */
public class ClassParser {

    public static JavaClass parse(JavaLexer lexer) throws JTAException {
        Set<JavaProperty> properties = parseHeader(lexer);
        String className = JavaParser.parseSimpleName(lexer);

        Set<JavaType> superClass, superInterface;
        if (lexer.nextIf(KeywordToken.Keyword._extends)) {
            superClass = parseTypes(lexer);
        } else {
            superClass = Collections.emptySet();
        }
        if (lexer.nextIf(KeywordToken.Keyword._implements)) {
            superInterface = parseTypes(lexer);
        } else {
            superInterface = Collections.emptySet();
        }


        lexer.next(BracketToken.CURLY_L);
        JavaParser.eatSemiColons(lexer);

        List<JavaClass.Member> members = new ArrayList<>();

        while (!lexer.peek().equals(BracketToken.CURLY_R)) {
            members.add(getMember(lexer));
            JavaParser.eatSemiColons(lexer);
            if (!lexer.hasNext()) {
                throw new JTAException.UnexpectedToken("'}'", "EOF");
            }
        }
        lexer.next(BracketToken.CURLY_R);

        return new JavaClass(properties, className, superClass, superInterface,members);
    }

    /**
     * Parse the header of the class,
     *
     * @param lexer the JavaLexer
     * @return properties of the class
     */
    private static Set<JavaProperty> parseHeader(JavaLexer lexer) throws JTAException {
        Token classToken = new KeywordToken(KeywordToken.Keyword._class);

        Set<JavaProperty> properties =
            JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS);

        lexer.next(classToken);
        return properties;
    }

    /**
     * Parse a member of the class
     *
     * @param lexer the JavaLexer
     * @return
     * @throws JTAException
     */
    private static JavaClass.Member getMember(JavaLexer lexer) throws JTAException {
        Token equal = new AssignmentOperator.Simple();

        lexer.createCheckPoint();
        boolean foundEqual = false;

        while (lexer.hasNext() && !lexer.peek().equals(BracketToken.CURLY_R)) {
            Token next = lexer.next();
            if (SplitterToken.isSemiColon(next)) {
                lexer.returnToLastCheckPoint();
                return FieldParser.parse(lexer);
            } else if (next.equals(BracketToken.CURLY_L) && !foundEqual) {
                // Because we found a '{' and did not encounter a '=', it is a function
                // if we found '=', we know it is a field. e.g. int[] a = {1, 2};
                lexer.returnToLastCheckPoint();
                return FunctionParser.parse(lexer);
            }

            if (next.equals(equal)) {
                foundEqual = true;
            }
        }

        if (lexer.hasNext()) {
            throw new JTAException.UnexpectedToken("class member", BracketToken.CURLY_R);
        } else {
            throw new JTAException.UnexpectedToken("class member", "EOF");
        }
    }

    private static Set<JavaType> parseTypes(JavaLexer lexer) throws JTAException {
        Set<JavaType> types = new HashSet<>();
        types.add(JavaParser.parseType(lexer, true));
        while (lexer.nextIf(SplitterToken.COMMA)) {
            types.add(JavaParser.parseType(lexer, true));
        }
        return types;
    }

}

