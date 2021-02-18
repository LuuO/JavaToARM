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
import javatoarm.token.operator.QuestColon;

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
            switch ((KeywordToken) token) {
                case _for:
                    return parseForLoop(lexer);

                case _switch:
                    return parseSwitch(lexer);

                case _do:
                    JavaCode bodyDo = CodeParser.parseCode(lexer);
                    lexer.next(KeywordToken._while);
                    JavaExpression conditionDo = parseConditionInBrackets(lexer);
                    return JavaLoop.doWhileLoop(bodyDo, conditionDo);

                case _if:
                    JavaExpression conditionIf = parseConditionInBrackets(lexer);
                    JavaCode bodyTrue = CodeParser.parseCode(lexer);
                    JavaCode bodyFalse = null;
                    if (lexer.nextIf(KeywordToken._else)) {
                        bodyFalse = CodeParser.parseCode(lexer);
                    }
                    return new JavaIfElse(conditionIf, bodyTrue, bodyFalse);

                case _while:
                    JavaExpression conditionWhile = parseConditionInBrackets(lexer);
                    JavaCode bodyWhile = CodeParser.parseCode(lexer);
                    return JavaLoop.whileLoop(bodyWhile, conditionWhile);

                case _synchronized:
                    JavaExpression lock;
                    if (lexer.nextIf(BracketToken.ROUND_L)) {
                        lock = ExpressionParser.parse(lexer);
                        lexer.next(BracketToken.ROUND_R);
                    } else {
                        lock = new JavaName("this");
                    }
                    JavaBlock bodySynchronized = CodeParser.parseBlock(lexer);
                    return new JavaSynchronized(lock, bodySynchronized);

                case _try:
                    JavaBlock tryBlock = CodeParser.parseBlock(lexer);
                    lexer.next(KeywordToken._catch);
                    List<JavaVariableDeclare> exceptions = FunctionParser.parseArgumentDeclares(lexer);
                    JavaBlock catchBlock = CodeParser.parseBlock(lexer);
                    return new JavaTryBlock(tryBlock, exceptions, catchBlock);
            }
        }
        throw new JTAException.UnexpectedToken("control token", token);
    }

    public static boolean isControlToken(Token token) {
        if (token instanceof KeywordToken) {
            return switch ((KeywordToken) token) {
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
            if (next.equals(CharToken.SEMI_COLON)) {
                lexer.returnToLastCheckPoint();
                return false;
            } else if (next.equals(QuestColon.COLON)) {
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
            lexer.next(QuestColon.COLON);
            JavaExpression collection = ExpressionParser.parse(lexer);
            lexer.next(BracketToken.ROUND_R);
            JavaCode body = CodeParser.parseCode(lexer);
            return new JavaEnhancedForLoop(elementType, elementName, collection, body);

        } else {
            lexer.next(BracketToken.ROUND_L);
            JavaStatement initial = null, increment = null;
            JavaExpression condition = null;
            if (!lexer.peek().equals(CharToken.SEMI_COLON)) {
                initial = StatementParser.parse(lexer);
            }
            lexer.next(CharToken.SEMI_COLON);
            if (!lexer.peek().equals(CharToken.SEMI_COLON)) {
                condition = ExpressionParser.parse(lexer);
            }
            lexer.next(CharToken.SEMI_COLON);
            if (!lexer.peek().equals(CharToken.SEMI_COLON)) {
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
            if (lexer.nextIf(KeywordToken._case)) {
                do {
                    ImmediateToken immediate = (ImmediateToken) lexer.next(ImmediateToken.class);
                    caseConditions.add(new ImmediateExpression(immediate));
                } while (lexer.nextIf(CharToken.COMMA));
                isDefault = false;

            } else if (lexer.nextIf(KeywordToken._default)) {
                isDefault = true;

            } else {
                throw new JTAException.UnexpectedToken("case, default or '}'", lexer.next());
            }
            lexer.next(QuestColon.COLON);

            List<JavaCode> body = new ArrayList<>();
            while (!lexer.peek().equals(KeywordToken._case)
                    && !lexer.peek().equals(KeywordToken._default)
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
