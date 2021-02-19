package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaAnnotation;
import javatoarm.javaast.JavaFile;
import javatoarm.javaast.JavaProperty;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaMember;
import javatoarm.parser.expression.ExpressionParser;
import javatoarm.token.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an object that can parse tokens into Java Abstract Syntax Tree
 */
public class JavaParser {
    private final JavaLexer lexer;

    /**
     * Initialize an instance of JavaParser
     *
     * @param lexer the lexer
     */
    public JavaParser(JavaLexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Eat all semi-colons that follow immediately
     *
     * @param lexer the lexer
     * @throws JTAException if an error occurs
     */
    public static void eatSemiColons(JavaLexer lexer) throws JTAException {
        // noinspection StatementWithEmptyBody
        while (lexer.hasNext() && lexer.nextIf(CharToken.SEMI_COLON)) ;
    }

    /**
     * Parse properties of a class, a variable, or a function. This method eats all keyword tokens
     * after the current position that declare properties immediately, and returns a set of these
     * properties.
     *
     * @param lexer     the lexer
     * @param validator if not null, uses the provided validator to validate the properties found.
     * @return a set of the properties. If no property token is found, returns an empty set.
     * @throws JTAException if an error occurs
     */
    public static Set<JavaProperty> parseProperties(JavaLexer lexer,
                                                    JavaProperty.Validator validator)
            throws JTAException {

        Set<JavaProperty> properties = new HashSet<>();

        for (; ; ) {
            JavaProperty property = JavaProperty.get(lexer.peek());
            if (property != null) {
                if (validator != null) {
                    validator.validate(property);
                }
                properties.add(property);
                lexer.next();
            } else {
                return properties;
            }
        }
    }

    /**
     * Parse the name of a declaration
     *
     * @param lexer the lexer
     * @return the name
     * @throws JTAException if an error occurs
     */
    public static String parseName(JavaLexer lexer) throws JTAException {
        return lexer.next(NameToken.class).toString();
    }

    /**
     * Parse a member in Java code. A member can be a variable, a function,
     * a field, or a member of some other member.
     *
     * @param lexer the lexer
     * @return the member
     * @throws JTAException if an error occurs
     */
    public static JavaMember parseMemberPath(JavaLexer lexer) throws JTAException {
        Token token = lexer.next();
        if (!(token instanceof NameToken) && !token.equals(KeywordToken._this)) {
            throw new JTAException.UnexpectedToken("name", token);
        }

        List<String> path = new ArrayList<>();
        path.add(token.toString());
        while (lexer.nextIf(CharToken.DOT)) {
            Token next = lexer.next();
            if (next instanceof NameToken) {
                path.add(next.toString());
            } else {
                lexer.rewind(2);
                break;
            }
        }
        return new JavaMember(path);
    }

    /**
     * Parse all annotations immediately after the current position
     *
     * @param lexer the lexer
     * @return a list of annotation. If there is not annotation immediately after
     * the current position, returns an empty list.
     * @throws JTAException if an error occurs
     */
    public static List<JavaAnnotation> parseAnnotations(JavaLexer lexer) throws JTAException {
        ArrayList<JavaAnnotation> annotations = new ArrayList<>();
        while (lexer.nextIf(CharToken.AT)) {
            JavaMember annotationType = parseMemberPath(lexer);
            if (lexer.nextIf(BracketToken.ROUND_L) && !lexer.nextIf(BracketToken.ROUND_R)) {
                JavaExpression parameter = ExpressionParser.parse(lexer);
                lexer.next(BracketToken.ROUND_R);

                annotations.add(new JavaAnnotation(annotationType, parameter));
            } else {
                annotations.add(new JavaAnnotation(annotationType));
            }
        }
        return annotations;
    }

    /**
     * Convert all tokens to Java AST
     *
     * @return the Java AST
     * @throws JTAException if an error occurs
     */
    public JavaFile toJavaAST() throws JTAException {
        return FileParser.parseFile(lexer);
    }

}
