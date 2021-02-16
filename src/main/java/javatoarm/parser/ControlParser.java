package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaBlock;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.control.*;
import javatoarm.javaast.expression.ImmediateExpression;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaName;
import javatoarm.javaast.statement.JavaStatement;
import javatoarm.javaast.statement.JavaVariableDeclare;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.*;
import javatoarm.token.operator.TernaryToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
                return parseForLoop(lexer);

            } else if (keywordToken.keyword == KeywordToken.Keyword._switch) {
                return parseSwitch(lexer);

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

    private static JavaCode parseForLoop(JavaLexer lexer) throws JTAException {
        if (isEnhancedFor(lexer)) {
            lexer.next(BracketToken.ROUND_L);
            JavaType elementType = TypeParser.parseType(lexer, false);
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
    }

    private static JavaSwitch parseSwitch(JavaLexer lexer) throws JTAException {
        lexer.next(BracketToken.ROUND_L);
        JavaExpression condition = ExpressionParser.parse(lexer);
        lexer.next(BracketToken.ROUND_R);
        lexer.next(BracketToken.CURLY_L);

        LinkedHashMap<List<ImmediateExpression>, List<JavaCode>> cases = new LinkedHashMap<>();
        List<JavaCode> defaultCase = null;
        while (!lexer.nextIf(BracketToken.CURLY_R)) {
            List<ImmediateExpression> caseConditions = new ArrayList<>();
            boolean isDefault;
            if (lexer.nextIf(KeywordToken.Keyword._case)) {
                do {
                    ImmediateToken immediate = (ImmediateToken) lexer.next(ImmediateToken.class);
                    caseConditions.add(new ImmediateExpression(immediate));
                } while (lexer.nextIf(SplitterToken.COMMA));
                isDefault = false;

            } else if (lexer.nextIf(KeywordToken.Keyword._default)) {
                isDefault = true;

            } else {
                throw new JTAException.UnexpectedToken("case, default or '}'", lexer.next());
            }
            lexer.next(TernaryToken.COLON);

            List<JavaCode> body = new ArrayList<>();
            while (!lexer.peek().equals(KeywordToken.Keyword._case)
                    && !lexer.peek().equals(KeywordToken.Keyword._default)
                    && !lexer.peek().equals(BracketToken.CURLY_R)) {

                body.add(CodeParser.parseCode(lexer));
                JavaParser.eatSemiColons(lexer);
            }

            if (isDefault) {
                if (defaultCase != null) {
                    throw new JTAException("There can be only only default block");
                }
                defaultCase = body;
            } else {
                cases.put(caseConditions, body);
            }
        }

        return new JavaSwitch(condition, cases, defaultCase);
    }
}
