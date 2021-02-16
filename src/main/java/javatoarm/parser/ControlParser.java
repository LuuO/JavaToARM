package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.*;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.expression.JavaName;
import javatoarm.java.statement.JavaStatement;
import javatoarm.java.statement.JavaVariableDeclare;
import javatoarm.java.type.JavaType;
import javatoarm.token.*;
import javatoarm.token.operator.TernaryToken;

import java.util.List;

/**
 * Represents control codes such as if-else and loops.
 */
public class ControlParser {
    public static JavaCode parse(JavaLexer lexer) throws JTAException {
        Token token = lexer.next();
        if (token instanceof KeywordToken) {
            KeywordToken keywordToken = (KeywordToken) token;

            if (keywordToken.keyword == KeywordToken.Keyword._for) {
                if (isEnhancedFor(lexer)) {
                    lexer.next(BracketToken.ROUND_L);
                    JavaType elementType = JavaParser.parseType(lexer, false);
                    String elementName = JavaParser.parseSimpleName(lexer);
                    lexer.next(TernaryToken.COLON);
                    JavaExpression collection = ExpressionParser.parse(lexer);
                    lexer.next(BracketToken.ROUND_R);
                    JavaCode body = CodeParser.parseCode(lexer);
                    return new JavaEnhancedForLoop(elementType, elementName, collection, body);

                } else {
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
                }

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
                    bodyFalse = CodeParser.parseCode(lexer);
                }
                return new JavaIfElse(condition, bodyTrue, bodyFalse);

            } else if (keywordToken.keyword == KeywordToken.Keyword._while) {
                JavaExpression condition = parseConditionInBrackets(lexer);
                JavaCode body = CodeParser.parseCode(lexer);
                return JavaLoop.whileLoop(body, condition);
            } else if (keywordToken.keyword == KeywordToken.Keyword._synchronized) {
                JavaExpression lock;
                if (lexer.nextIf(BracketToken.ROUND_L)) {
                    lock = ExpressionParser.parse(lexer);
                    lexer.next(BracketToken.ROUND_R);
                } else {
                    lock = new JavaName("this");
                }
                JavaBlock body = CodeParser.parseBlock(lexer);
                return new JavaSynchronized(lock, body);
            } else if (keywordToken.keyword == KeywordToken.Keyword._try) {
                JavaBlock tryBlock = CodeParser.parseBlock(lexer);
                lexer.next(new KeywordToken(KeywordToken.Keyword._catch));
                List<JavaVariableDeclare> exceptions = FunctionParser.parseArgumentDeclares(lexer);
                JavaBlock catchBlock = CodeParser.parseBlock(lexer);
                return new JavaTryBlock(tryBlock, exceptions, catchBlock);
            }
        }

        throw new JTAException.UnexpectedToken("control token", token);
    }

    public static boolean isControlToken(Token token) {
        if (token instanceof KeywordToken) {
            KeywordToken.Keyword keyword = ((KeywordToken) token).keyword;
            return switch (keyword) {
                case _for, _switch, _do, _if, _while, _synchronized, _try -> true;
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

    private static boolean isEnhancedFor(JavaLexer lexer) throws JTAException {
        lexer.createCheckPoint();
        lexer.next(BracketToken.ROUND_L);
        while (lexer.hasNext()) {
            Token next = lexer.next();
            if (next.equals(SplitterToken.SEMI_COLON)) {
                lexer.returnToLastCheckPoint();
                return false;
            } else if (next.equals(TernaryToken.COLON)) {
                lexer.returnToLastCheckPoint();
                return true;
            }
        }
        throw new JTAException.UnexpectedToken("for loop", "EOF");
    }
}
