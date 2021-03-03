package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.NewArrayExpression;
import javatoarm.javaast.expression.NewObjectExpression;
import javatoarm.javaast.type.JavaType;
import javatoarm.parser.expression.ExpressionParser;
import javatoarm.token.*;

import java.util.ArrayList;
import java.util.List;

public class RightValueParser {

    /**
     * Parse the next expression, which an object/array initialization.
     *
     * @param lexer the lexer
     * @return an initialization expression
     * @throws JTAException if an error occurs
     */
    public static JavaExpression parseNewInit(JavaLexer lexer) throws JTAException {
        lexer.next(KeywordToken._new);
        JavaType type = TypeParser.parseType(lexer, false);
        Token next = lexer.next();

        if (next.equals(BracketToken.SQUARE_L)) {
            /* initializing an array */
            if (lexer.nextIf(BracketToken.SQUARE_R)) {
                /* unspecified size, has a predetermined list of values */
                lexer.next(BracketToken.CURLY_L);
                List<JavaExpression> values = new ArrayList<>();
                if (!lexer.peek(BracketToken.CURLY_R)) {
                    values.add(ExpressionParser.parse(lexer));
                    while (lexer.peek(SymbolToken.COMMA)) {
                        values.add(ExpressionParser.parse(lexer));
                    }
                }
                lexer.next(BracketToken.CURLY_R);
                return new NewArrayExpression(type, values);
            } else {
                /* specified size */
                JavaExpression size = ExpressionParser.parse(lexer);
                lexer.next(BracketToken.SQUARE_R);
                return new NewArrayExpression(type, size);
            }

        } else if (next.equals(BracketToken.ROUND_L)) {
            /* initializing an object */
            lexer.rewind();
            List<JavaRightValue> arguments = FunctionParser.parseCallArguments(lexer);
            return new NewObjectExpression(type, arguments);

        } else {
            throw new JTAException.UnexpectedToken("'[' or ')'", next);
        }
    }
}
