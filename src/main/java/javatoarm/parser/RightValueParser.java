package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaNewArray;
import javatoarm.java.JavaRightValue;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.expression.NewObjectExpression;
import javatoarm.java.type.JavaType;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.Token;

import java.util.List;

public class RightValueParser {

    static JavaRightValue parseNewInit(JavaLexer lexer) throws JTAException {
        lexer.next(new KeywordToken(KeywordToken.Keyword._new));
        JavaType type = JavaParser.parseType(lexer, false);
        Token next = lexer.next();
        if (next.equals(BracketToken.SQUARE_L)) {
            /* array */
            JavaExpression size = ExpressionParser.parse(lexer);
            lexer.next(BracketToken.SQUARE_R);
            return new JavaNewArray(type, size);

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
