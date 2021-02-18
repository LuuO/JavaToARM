package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.expression.JavaName;
import javatoarm.javaast.type.*;
import javatoarm.token.*;
import javatoarm.token.operator.QuestColon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeParser {

    /**
     * Parse a Java data type
     *
     * @param lexer       the lexer
     * @param acceptArray true if an array is type is acceptable, false otherwise.
     * @return the data type
     * @throws JTAException if an error occurs
     */
    public static JavaType parseType(JavaLexer lexer, boolean acceptArray) throws JTAException {
        JavaType type;
        Token token = lexer.next();
        if (token instanceof KeywordToken) {
            type = JavaSimpleType.get((KeywordToken) token);
            if (type == null) {
                throw new JTAException.UnexpectedToken("data type", token);
            }

        } else if (token instanceof NameToken) {
            lexer.rewind();
            JavaName typeName = JavaParser.parseNamePath(lexer);

            /* check type parameter */
            if (lexer.peek().equals(AngleToken.LEFT)) {
                type = new JavaParametrizedType(typeName, parseTypeParameters(lexer));
            } else {
                type = JavaSimpleType.get(typeName);
            }

        } else {
            throw new JTAException.UnexpectedToken("data type", token);
        }
        /* check varargs */
        if (lexer.nextIf(CharToken.DOT)) {
            lexer.next(CharToken.DOT);
            lexer.next(CharToken.DOT);
            type = new JavaArrayType(type);
        } else {
            while (acceptArray && lexer.nextIf(BracketToken.SQUARE_L)) {
                lexer.next(BracketToken.SQUARE_R);
                type = new JavaArrayType(type);
            }
        }
        return type;
    }

    /**
     * Parse type parameters which are enclosed in &lt&gt .
     * Examples: &lt Integer &gt, &lt? extend Object&gt, &lt Integer, String &gt
     *
     * @param lexer the lexer
     * @return a lists of type parameters. If there are 0 parameters, returns an empty immutable list.
     * @throws JTAException if an error occurs
     */
    public static List<JavaType> parseTypeParameters(JavaLexer lexer) throws JTAException {
        lexer.next(AngleToken.LEFT);
        if (lexer.nextIf(AngleToken.RIGHT)) {
            return Collections.emptyList();
        }

        List<JavaType> typeParameters = new ArrayList<>();

        do {
            if (lexer.nextIf(QuestColon.QUESTION)) {
                JavaTypeWildcard.Bound bound;
                JavaType wildcardType;
                if (lexer.nextIf(KeywordToken._extends)) {
                    bound = JavaTypeWildcard.Bound.EXTEND;
                    wildcardType = parseType(lexer, false);
                } else if (lexer.nextIf(KeywordToken._super)) {
                    bound = JavaTypeWildcard.Bound.SUPER;
                    wildcardType = parseType(lexer, false);
                } else {
                    bound = JavaTypeWildcard.Bound.UNBOUNDED;
                    wildcardType = null;
                }
                typeParameters.add(new JavaTypeWildcard(bound, wildcardType));
            } else {
                typeParameters.add(parseType(lexer, false));
            }
        } while (lexer.nextIf(CharToken.COMMA));
        lexer.next(AngleToken.RIGHT);

        return typeParameters;
    }
}
