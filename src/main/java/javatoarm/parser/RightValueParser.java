package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaNewArray;
import javatoarm.javaast.expression.NewObjectExpression;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.*;

import java.util.ArrayList;
import java.util.List;

public class RightValueParser {

    public static JavaExpression parseNewInit(JavaLexer lexer) throws JTAException {
        lexer.next(KeywordToken.Keyword._new);
        JavaType type = TypeParser.parseType(lexer, false);
        Token next = lexer.next();
        if (next.equals(BracketToken.SQUARE_L)) {
            /* array */
            if (lexer.nextIf(BracketToken.SQUARE_R)) {
                lexer.next(BracketToken.CURLY_L);
                List<JavaExpression> values = new ArrayList<>();
                if (!lexer.peek().equals(BracketToken.CURLY_R)) {
                    values.add(ExpressionParser.parse(lexer));
                    while (lexer.peek().equals(CharToken.COMMA)) {
                        values.add(ExpressionParser.parse(lexer));
                    }
                }
                lexer.next(BracketToken.CURLY_R);
                return new JavaNewArray(type, values);
            } else {
                JavaExpression size = ExpressionParser.parse(lexer);
                lexer.next(BracketToken.SQUARE_R);
                return new JavaNewArray(type, size);
            }

        } else if (next.equals(BracketToken.ROUND_L)) {
            /* initialize objects */
            lexer.rewind();
            List<JavaRightValue> arguments = FunctionParser.parseCallArguments(lexer);
            return new NewObjectExpression(type, arguments);

        } else {
            throw new JTAException.UnexpectedToken("'[' or ')'", next);
        }
    }
}
