package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaAnnotation;
import javatoarm.javaast.JavaClass;
import javatoarm.javaast.JavaClassMember;
import javatoarm.javaast.JavaProperty;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.*;
import javatoarm.token.operator.AssignmentOperator;

import java.util.*;

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

        List<JavaClassMember> members = new ArrayList<>();

        while (!lexer.peek().equals(BracketToken.CURLY_R)) {
            members.add(getMember(lexer, className));
            JavaParser.eatSemiColons(lexer);
            if (!lexer.hasNext()) {
                throw new JTAException.UnexpectedToken("'}'", "EOF");
            }
        }
        lexer.next(BracketToken.CURLY_R);

        return new JavaClass(properties, className, superClass, superInterface, members);
    }

    /**
     * Parse the header of the class,
     *
     * @param lexer the JavaLexer
     * @return properties of the class
     */
    private static Set<JavaProperty> parseHeader(JavaLexer lexer) throws JTAException {
        Set<JavaProperty> properties =
                JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS);

        lexer.next(KeywordToken.Keyword._class);
        return properties;
    }

    /**
     * Parse a member of the class
     *
     * @param lexer the JavaLexer
     * @return a parsed class member
     * @throws JTAException if errors occur
     */
    private static JavaClassMember getMember(JavaLexer lexer, String className)
            throws JTAException {
        List<JavaAnnotation> annotations;
        if (lexer.peek().equals(AnnotationToken.INSTANCE)) {
            annotations = JavaParser.parseAnnotations(lexer);
        } else {
            annotations = Collections.emptyList();
        }
        switch (getNextMemberType(lexer)) {
            case FIELD:
                return FieldParser.parse(lexer, annotations);
            case FUNCTION:
                return FunctionParser.parse(lexer, className, annotations);
            case CLASS:
                return ClassParser.parse(lexer);
            case INITIALIZER:
                return new JavaClass.Initializer(CodeParser.parseBlock(lexer), false);
            case STATIC_INITIALIZER:
                lexer.next(KeywordToken.Keyword._static);
                return new JavaClass.Initializer(CodeParser.parseBlock(lexer), true);
            default:
                throw new AssertionError();
        }
    }

    private static MemberType getNextMemberType(JavaLexer lexer) throws JTAException {
        if (lexer.peek().equals(BracketToken.CURLY_L)) {
            return MemberType.INITIALIZER;
        }

        lexer.createCheckPoint();
        if (lexer.nextIf(KeywordToken.Keyword._static) && lexer.nextIf(BracketToken.CURLY_L)) {
            lexer.returnToLastCheckPoint();
            return MemberType.STATIC_INITIALIZER;
        }
        lexer.returnToLastCheckPoint();

        lexer.createCheckPoint();
        while (lexer.hasNext() && !lexer.peek().equals(BracketToken.CURLY_R)) {
            Token next = lexer.next();
            if (next.equals(KeywordToken.Keyword._class)) {

                lexer.returnToLastCheckPoint();
                return MemberType.CLASS;
            } else if (next.equals(KeywordToken.Keyword._native)) {
                // TODO: Assuming all native members are functions
                lexer.returnToLastCheckPoint();
                return MemberType.FUNCTION;
            } else if (next.equals(CharToken.SEMI_COLON)) {
                lexer.returnToLastCheckPoint();
                return MemberType.FIELD;
            } else if (next.equals(BracketToken.CURLY_L)) {
                // Because we found a '{' and did not encounter a '=', it is a function
                // if we found '=', we know it is a field. e.g. int[] a = {1, 2};
                lexer.returnToLastCheckPoint();
                return MemberType.FUNCTION;
            } else if (next instanceof AssignmentOperator.Simple) {
                lexer.returnToLastCheckPoint();
                return MemberType.FIELD;
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
        types.add(TypeParser.parseType(lexer, true));
        while (lexer.nextIf(CharToken.COMMA)) {
            types.add(TypeParser.parseType(lexer, true));
        }
        return types;
    }

    private enum MemberType {
        FIELD, FUNCTION, CLASS, INITIALIZER, STATIC_INITIALIZER
    }

}

