package javatoarm.parser.expression;

import javatoarm.JTAException;
import javatoarm.javaast.JavaBlock;
import javatoarm.javaast.JavaLambda;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.parser.CodeParser;
import javatoarm.token.*;

import java.util.ArrayList;
import java.util.List;

public class LambdaParser {

    /**
     * Check if the following expression is a lambda expression
     *
     * @param lexer the lexer
     * @return true if the following expression is a lambda expression, false otherwise
     * @throws JTAException if an error occurs
     */
    public static boolean isLambda(JavaLexer lexer) throws JTAException {
        lexer.createCheckPoint();
        while (lexer.hasNext()) {
            Token token = lexer.next();
            if (token instanceof ArrowToken) {
                /* arrow token -> is lambda */
                lexer.returnToLastCheckPoint();
                return true;
            } else if (!token.equals(BracketToken.ROUND_L) && !token.equals(BracketToken.ROUND_R)
                    && !token.equals(SymbolToken.COMMA) && !(token instanceof NameToken)) {
                /* contains something that cannot be a part of lambda declaration */
                lexer.returnToLastCheckPoint();
                return false;
            }
        }
        lexer.returnToLastCheckPoint();
        return false;
    }

    /**
     * Parse a lambda expression
     *
     * @param lexer the lexer
     * @return a lambda expression
     * @throws JTAException if an error occurs
     */
    public static JavaLambda parse(JavaLexer lexer) throws JTAException {
        List<String> parameters = new ArrayList<>();
        if (lexer.nextIf(BracketToken.ROUND_L)) {
            /* multiple parameters */
            do {
                parameters.add(lexer.next(NameToken.class).toString());
            } while (lexer.nextIf(SymbolToken.COMMA));
        } else {
            /* one parameter */
            parameters.add(lexer.next(NameToken.class).toString());
        }

        lexer.next(ArrowToken.INSTANCE);

        if (lexer.peek(BracketToken.CURLY_L)) {
            /* block */
            return new JavaLambda(parameters, CodeParser.parseBlock(lexer));
        } else {
            /* expression */
            return new JavaLambda(parameters,  ExpressionParser.parse(lexer));
        }
    }

}
