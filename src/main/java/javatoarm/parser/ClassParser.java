package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.*;
import javatoarm.javaast.statement.VariableDeclareStatement;
import javatoarm.javaast.type.JavaType;
import javatoarm.parser.expression.ExpressionParser;
import javatoarm.token.*;
import javatoarm.token.operator.AssignmentOperator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Methods for parsing a Java Class
 */
public class ClassParser {

    /**
     * Parse a Java class
     *
     * @param lexer the lexer
     * @return the Java class AST
     * @throws JTAException if an error occurs
     */
    public static JavaClass parse(JavaLexer lexer) throws JTAException {
        Set<JavaProperty> properties =
                JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS);

        lexer.next(KeywordToken._class);

        String className = JavaParser.parseName(lexer);

        JavaType superClass = lexer.nextIf(KeywordToken._extends)
                ? TypeParser.parseType(lexer, false)
                : null;
        Set<JavaType> superInterface = lexer.nextIf(KeywordToken._implements) ? parseTypes(lexer) : Set.of();

        lexer.next(BracketToken.CURLY_L);
        JavaParser.eatSemiColons(lexer);

        List<JavaClassMember> members = new ArrayList<>();
        while (!lexer.nextIf(BracketToken.CURLY_R)) {
            members.add(getMember(lexer, className));
            JavaParser.eatSemiColons(lexer);
        }

        return new JavaClass(className, properties, superClass, superInterface, members);
    }

    /**
     * Parse a member of the class
     *
     * @param lexer the JavaLexer
     * @return a parsed class member
     * @throws JTAException if an error occurs
     */
    private static JavaClassMember getMember(JavaLexer lexer, String className)
            throws JTAException {
        List<JavaAnnotation> annotations = lexer.peek(SymbolToken.AT)
                ? JavaParser.parseAnnotations(lexer)
                : List.of();

        return switch (getNextMemberType(lexer)) {
            case FIELD -> parseField(lexer, annotations);
            case FUNCTION -> FunctionParser.parse(lexer, className, annotations);
            case CLASS -> ClassParser.parse(lexer);
            case INITIALIZER, STATIC_INITIALIZER -> parseInitializer(lexer);
        };
    }

    /**
     * Parse a class initializer block.
     *
     * @param lexer the lexer
     * @return the initializer
     * @throws JTAException if an error occurs
     */
    private static JavaClass.Initializer parseInitializer(JavaLexer lexer) throws JTAException {
        boolean isStatic = lexer.nextIf(KeywordToken._static);
        return new JavaClass.Initializer(CodeParser.parseBlock(lexer), isStatic);
    }

    /**
     * Check the member type of next member
     *
     * @param lexer the lexer
     * @return the member type of next member
     * @throws JTAException if an error occurs
     */
    private static MemberType getNextMemberType(JavaLexer lexer) throws JTAException {
        if (lexer.peek(BracketToken.CURLY_L)) {
            return MemberType.INITIALIZER;
        }

        lexer.createCheckPoint();
        if (lexer.nextIf(KeywordToken._static) && lexer.nextIf(BracketToken.CURLY_L)) {
            lexer.returnToLastCheckPoint();
            return MemberType.STATIC_INITIALIZER;
        }
        lexer.returnToLastCheckPoint();

        lexer.createCheckPoint();
        while (lexer.hasNext() && !lexer.peek(BracketToken.CURLY_R)) {
            Token next = lexer.next();
            if (next.equals(KeywordToken._class)) {
                lexer.returnToLastCheckPoint();
                return MemberType.CLASS;

            } else if (next.equals(KeywordToken._native) || next.equals(BracketToken.CURLY_L)) {
                /* Assuming all native members are functions */
                lexer.returnToLastCheckPoint();
                return MemberType.FUNCTION;

            } else if (next.equals(SymbolToken.SEMI_COLON) || next instanceof AssignmentOperator.Simple) {
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

    /**
     * Parse a set of types separated by commas.
     *
     * @param lexer the lexer
     * @return a set of types
     * @throws JTAException if an error occurs
     */
    private static Set<JavaType> parseTypes(JavaLexer lexer) throws JTAException {
        Set<JavaType> types = new HashSet<>();
        do {
            types.add(TypeParser.parseType(lexer, true));
        } while (lexer.nextIf(SymbolToken.COMMA));
        return types;
    }

    /**
     * Parse a field member declaration.
     *
     * @param lexer       the lexer
     * @param annotations annotations of the field
     * @return return the field declaration
     * @throws JTAException if an error occurs
     */
    private static VariableDeclareStatement parseField(JavaLexer lexer, List<JavaAnnotation> annotations)
            throws JTAException {
        Set<JavaProperty> properties =
                JavaParser.parseProperties(lexer, JavaProperty.Validator.CLASS_MEMBER);
        JavaType type = TypeParser.parseType(lexer, true);
        String name = JavaParser.parseName(lexer);

        JavaRightValue initialValue = lexer.nextIf(AssignmentOperator.Simple.INSTANCE)
                ? ExpressionParser.parse(lexer)
                : null;

        if (!lexer.nextIf(SymbolToken.SEMI_COLON)) {
            throw new JTAException.UnexpectedToken("';' or '='", lexer.peek());
        }

        return new VariableDeclareStatement(type, name, initialValue, properties, annotations);
    }

    /**
     * Representing the type of member
     */
    private enum MemberType {
        FIELD, FUNCTION, CLASS, INITIALIZER, STATIC_INITIALIZER
    }

}

