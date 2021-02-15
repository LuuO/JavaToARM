package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.expression.JavaNewArray;
import javatoarm.java.expression.NewObjectExpression;
import javatoarm.java.type.JavaType;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;

import java.util.ArrayList;
import java.util.List;

public class RightValueParser {

    public static JavaExpression parseNewInit(JavaLexer lexer) throws JTAException {
        lexer.next(new KeywordToken(KeywordToken.Keyword._new));
        JavaType type = JavaParser.parseType(lexer, false);
        Token next = lexer.next();
        if (next.equals(BracketToken.SQUARE_L)) {
            /* array */
            if (lexer.nextIf(BracketToken.SQUARE_R)) {
                lexer.next(BracketToken.CURLY_L);
                List<JavaExpression> values = new ArrayList<>();
                if (!lexer.peek().equals(BracketToken.CURLY_R)) {
                    values.add(ExpressionParser.parse(lexer));
                    while (lexer.peek().equals(SplitterToken.COMMA)) {
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
            List<JavaExpression> arguments = FunctionParser.parseCallArguments(lexer);
            return new NewObjectExpression(type, arguments);

        } else {
            throw new JTAException.UnexpectedToken("'[' or ')'", next);
        }
    }
}
