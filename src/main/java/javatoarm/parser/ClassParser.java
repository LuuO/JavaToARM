package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaClass;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;
import javatoarm.token.operator.AssignmentOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods for parsing a Java Class
 */
public class ClassParser {

    public static JavaClass parse(JavaLexer lexer) throws JTAException {
        boolean isPublic = parseAccess(lexer);
        String className = JavaParser.parseSimpleName(lexer);

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

        return new JavaClass(isPublic, className, members);
    }

    /**
     * Parse the header of the class,
     *
     * @param lexer the JavaLexer
     * @return true if the class is public, false if the class is package-private.
     */
    private static boolean parseAccess(JavaLexer lexer) throws JTAException {
        Token classToken = new KeywordToken(KeywordToken.Keyword._class);
        Token publicToken = new KeywordToken(KeywordToken.Keyword._public);

        Token nextToken = lexer.next();
        if (nextToken.equals(classToken)) {
            return false;
        } else if (nextToken.equals(publicToken)) {
            lexer.next(classToken);
            return true;
        } else {
            throw new JTAException.UnexpectedToken(
                "class, public, or package-private", nextToken.toString());
        }
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

}

