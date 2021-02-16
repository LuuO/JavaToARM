package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.expression.JavaName;
import javatoarm.javaast.type.*;
import javatoarm.token.*;
import javatoarm.token.operator.TernaryToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeParser {

    public static JavaType parseType(JavaLexer lexer, boolean checkIsArray) throws JTAException {
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
        if (lexer.nextIf(MemberAccessToken.INSTANCE)) {
            lexer.next(MemberAccessToken.INSTANCE);
            lexer.next(MemberAccessToken.INSTANCE);
            type = new JavaArrayType(type);
        } else {
            while (checkIsArray && lexer.nextIf(BracketToken.SQUARE_L)) {
                lexer.next(BracketToken.SQUARE_R);
                type = new JavaArrayType(type);
            }
        }
        return type;
    }

    public static List<JavaType> parseTypeParameters(JavaLexer lexer) throws JTAException {
        lexer.next(AngleToken.LEFT);
        if (lexer.nextIf(AngleToken.RIGHT)) {
            return Collections.emptyList();
        }

        List<JavaType> typeParameters = new ArrayList<>();

        do {
            if (lexer.nextIf(TernaryToken.QUESTION)) {
                JavaTypeWildcard.Bound bound;
                JavaType wildcardType;
                if (lexer.nextIf(KeywordToken.Keyword._extends)) {
                    bound = JavaTypeWildcard.Bound.EXTEND;
                    wildcardType = parseType(lexer, false);
                } else if (lexer.nextIf(KeywordToken.Keyword._super)) {
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
        } while (lexer.nextIf(SplitterToken.COMMA));
        lexer.next(AngleToken.RIGHT);

        return typeParameters;
    }
}
