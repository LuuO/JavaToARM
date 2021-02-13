package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaCode;
import javatoarm.java.JavaIfElse;
import javatoarm.java.JavaLoop;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.statement.JavaStatement;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;

/**
 * Represents control codes such as if-else and loops.
 */
public class ControlParser {
    public static JavaCode parse(JavaLexer lexer) throws JTAException {
        Token token = lexer.next();
        if (token instanceof KeywordToken) {
            KeywordToken keywordToken = (KeywordToken) token;

            if (keywordToken.keyword == KeywordToken.Keyword._for) {
                lexer.next(BracketToken.ROUND_L);
                JavaStatement initial = null, increment = null;
                JavaExpression condition = null;
                if (!lexer.peek().equals(SplitterToken.SEMI_COLON)) {
                    initial = StatementParser.parse(lexer);
                }
                lexer.next(SplitterToken.SEMI_COLON);
                if (!lexer.peek().equals(SplitterToken.SEMI_COLON)) {
                    condition = ExpressionParser.parse(lexer);
                }
                lexer.next(SplitterToken.SEMI_COLON);
                if (!lexer.peek().equals(SplitterToken.SEMI_COLON)) {
                    increment = StatementParser.parse(lexer);
                }
                lexer.next(BracketToken.ROUND_R);
                JavaCode body = CodeParser.parseCode(lexer);
                return JavaLoop.forLoop(body, initial, condition, increment);

            } else if (keywordToken.keyword == KeywordToken.Keyword._switch) {
                // TODO: support switch statement
                throw new JTAException.Unsupported("switch");

            } else if (keywordToken.keyword == KeywordToken.Keyword._do) {
                JavaCode body = CodeParser.parseCode(lexer);
                lexer.next(new KeywordToken(KeywordToken.Keyword._while));
                JavaExpression condition = parseConditionInBrackets(lexer);
                return JavaLoop.doWhileLoop(body, condition);

            } else if (keywordToken.keyword == KeywordToken.Keyword._if) {
                JavaExpression condition = parseConditionInBrackets(lexer);
                JavaCode bodyTrue = CodeParser.parseCode(lexer);
                JavaCode bodyFalse = null;
                if (lexer.nextIf(KeywordToken.Keyword._else)) {
                    bodyFalse = CodeParser.parseBlock(lexer);
                }
                return new JavaIfElse(condition, bodyTrue, bodyFalse);

            } else if (keywordToken.keyword == KeywordToken.Keyword._while) {
                JavaExpression condition = parseConditionInBrackets(lexer);
                JavaCode body = CodeParser.parseCode(lexer);
                return JavaLoop.whileLoop(body, condition);
            }
        }

        throw new JTAException.UnexpectedToken("control token", token);
    }

    public static boolean isControlToken(Token token) {
        if (token instanceof KeywordToken) {
            KeywordToken.Keyword keyword = ((KeywordToken) token).keyword;
            return switch (keyword) {
                case _for, _switch, _do, _if, _while -> true;
                default -> false;
            };
        }
        return false;
    }

    private static JavaExpression parseConditionInBrackets(JavaLexer lexer) throws JTAException {
        lexer.next(BracketToken.ROUND_L);
        JavaExpression condition = ExpressionParser.parse(lexer);
        lexer.next(BracketToken.ROUND_R);
        return condition;
    }
}
