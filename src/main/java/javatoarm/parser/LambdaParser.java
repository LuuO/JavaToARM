package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaBlock;
import javatoarm.javaast.JavaLambda;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.token.*;

import java.util.ArrayList;
import java.util.List;

public class LambdaParser {

    public static JavaExpression parseExpressionOrLambda(JavaLexer lexer) throws JTAException {
        if (isLambda(lexer)) {
            return parseLambda(lexer);
        } else {
            return ExpressionParser.parse(lexer);
        }
    }

    private static boolean isLambda(JavaLexer lexer) throws JTAException {
        lexer.createCheckPoint();
        while (lexer.hasNext()) {
            Token token = lexer.next();
            if (token instanceof ArrowToken) {
                lexer.returnToLastCheckPoint();
                return true;
            } else if (!token.equals(BracketToken.ROUND_L) && !token.equals(BracketToken.ROUND_R)
                    && !token.equals(SplitterToken.COMMA) && !(token instanceof NameToken)) {
                lexer.returnToLastCheckPoint();
                return false;
            }
        }
        lexer.returnToLastCheckPoint();
        return false;
    }

    private static JavaLambda parseLambda(JavaLexer lexer) throws JTAException {
        List<String> parameters = new ArrayList<>();
        if (lexer.nextIf(BracketToken.ROUND_L)) {
            do {
                parameters.add(lexer.next(NameToken.class).toString());
            } while (lexer.nextIf(SplitterToken.COMMA));
        } else {
            parameters.add(lexer.next(NameToken.class).toString());
        }
        lexer.next(ArrowToken.INSTANCE);
        if (lexer.peek().equals(BracketToken.CURLY_L)) {
            JavaBlock body = CodeParser.parseBlock(lexer);
            return new JavaLambda(parameters, body);
        } else {
            JavaExpression body = ExpressionParser.parse(lexer);
            return new JavaLambda(parameters, body);
        }
    }

}
