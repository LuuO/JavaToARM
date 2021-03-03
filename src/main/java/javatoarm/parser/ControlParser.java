package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaBlock;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.control.*;
import javatoarm.javaast.expression.ImmediateExpression;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.JavaMember;
import javatoarm.javaast.statement.JavaStatement;
import javatoarm.javaast.statement.VariableDeclareStatement;
import javatoarm.javaast.type.JavaType;
import javatoarm.parser.expression.ExpressionParser;
import javatoarm.token.*;
import javatoarm.token.operator.QuestColon;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Parsers for control codes such as if-else and loops.
 */
public class ControlParser {

    /**
     * Parse a control statement
     *
     * @param lexer the lexer
     * @return an instance of JavaCode representing the control block.
     * @throws JTAException if an error occurs
     */
    public static JavaCode parse(JavaLexer lexer) throws JTAException {
        Token token = lexer.next();
        if (token instanceof KeywordToken) {
            switch ((KeywordToken) token) {
                case _for:
                    return parseForLoop(lexer);

                case _switch:
                    return parseSwitch(lexer);

                case _do: {
                    JavaCode body = CodeParser.parseCode(lexer);
                    lexer.next(KeywordToken._while);
                    JavaExpression condition = parseConditionInBrackets(lexer);
                    return new JavaLoop.DoWhile(condition, body);
                }
                case _if: {
                    JavaExpression conditionIf = parseConditionInBrackets(lexer);
                    JavaCode bodyTrue = CodeParser.parseCode(lexer);
                    JavaCode bodyFalse = lexer.nextIf(KeywordToken._else)
                            ? CodeParser.parseCode(lexer)
                            : null;
                    return new JavaIfElse(conditionIf, bodyTrue, bodyFalse);
                }
                case _while: {
                    JavaExpression condition = parseConditionInBrackets(lexer);
                    JavaCode body = CodeParser.parseCode(lexer);
                    return new JavaLoop.While(condition, body);
                }
                case _synchronized: {
                    JavaExpression lock;
                    if (lexer.nextIf(BracketToken.ROUND_L)) {
                        lock = ExpressionParser.parse(lexer);
                        lexer.next(BracketToken.ROUND_R);
                    } else {
                        lock = new JavaMember("this");
                    }
                    JavaBlock bodySynchronized = CodeParser.parseBlock(lexer);
                    return new JavaSynchronized(lock, bodySynchronized);
                }
                case _try: {
                    JavaBlock tryBlock = CodeParser.parseBlock(lexer);
                    lexer.next(KeywordToken._catch);
                    List<VariableDeclareStatement> exceptions = FunctionParser.parseArgumentDeclares(lexer);
                    JavaBlock catchBlock = CodeParser.parseBlock(lexer);
                    return new JavaTryBlock(tryBlock, exceptions, catchBlock);
                }
            }
        }
        throw new JTAException.UnexpectedToken("control token", token);
    }

    /**
     * Test if the token indicates a control statement.
     *
     * @param token the token
     * @return true if the token indicates a control statement, false otherwise.
     */
    public static boolean isControlToken(Token token) {
        if (token instanceof KeywordToken) {
            return switch ((KeywordToken) token) {
                case _for, _switch, _do, _if, _while, _synchronized, _try -> true;
                default -> false;
            };
        }
        return false;
    }

    /**
     * Parse a conditional expression surrounded by round brackets.
     *
     * @param lexer the lexer
     * @return the conditional expression
     * @throws JTAException if an error occurs
     */
    private static JavaExpression parseConditionInBrackets(JavaLexer lexer) throws JTAException {
        lexer.next(BracketToken.ROUND_L);
        JavaExpression condition = ExpressionParser.parse(lexer);
        lexer.next(BracketToken.ROUND_R);
        return condition;
    }

    /**
     * Test if the following is an enhanced for loop.
     * Example of an enhanced for loop: for (Integer a : intList) {}
     *
     * @param lexer the lexer
     * @return true if the following is an enhanced for loop, false otherwise.
     * @throws JTAException if an error occurs
     */
    private static boolean isEnhancedFor(JavaLexer lexer) throws JTAException {
        lexer.createCheckPoint();
        lexer.next(BracketToken.ROUND_L);
        while (lexer.hasNext()) {
            Token next = lexer.next();
            if (next.equals(SymbolToken.SEMI_COLON)) {
                lexer.returnToLastCheckPoint();
                return false;
            } else if (next.equals(QuestColon.COLON)) {
                lexer.returnToLastCheckPoint();
                return true;
            }
        }
        throw new JTAException.UnexpectedToken("for loop", "EOF");
    }

    /**
     * Parse a for loop. This method starts from the token immediately after the "for" keyword token.
     *
     * @param lexer the lexer
     * @return the for loop syntax tree, an instance of JavaEnhancedForLoop or JavaLoop
     * @throws JTAException if an error occurs
     */
    private static JavaCode parseForLoop(JavaLexer lexer) throws JTAException {
        if (isEnhancedFor(lexer)) {
            /* for (Element e : list) */
            lexer.next(BracketToken.ROUND_L);
            JavaType elementType = TypeParser.parseType(lexer, false);
            String elementName = JavaParser.parseName(lexer);
            lexer.next(QuestColon.COLON);
            JavaExpression collection = ExpressionParser.parse(lexer);
            lexer.next(BracketToken.ROUND_R);
            JavaCode body = CodeParser.parseCode(lexer);
            return new JavaEnhancedForLoop(elementType, elementName, collection, body);

        } else {
            /* for (int i = 0; i < max; i++) */
            lexer.next(BracketToken.ROUND_L);

            JavaStatement initial = lexer.peek(SymbolToken.SEMI_COLON)
                    ? null : StatementParser.parse(lexer);
            lexer.next(SymbolToken.SEMI_COLON);
            JavaExpression condition = lexer.peek(SymbolToken.SEMI_COLON)
                    ? null : ExpressionParser.parse(lexer);
            lexer.next(SymbolToken.SEMI_COLON);
            JavaStatement increment = lexer.peek(BracketToken.ROUND_R)
                    ? null : StatementParser.parse(lexer);
            lexer.next(BracketToken.ROUND_R);

            JavaCode body = CodeParser.parseCode(lexer);
            return new JavaLoop.For(initial, condition, increment, body);
        }
    }

    /**
     * Parse a switch statement. This method starts from the token immediately after the "switch" keyword token.
     *
     * @param lexer the lexer
     * @return the switch syntax tree
     * @throws JTAException if an error occurs
     */
    private static JavaSwitch parseSwitch(JavaLexer lexer) throws JTAException {
        lexer.next(BracketToken.ROUND_L);
        JavaExpression condition = ExpressionParser.parse(lexer);
        lexer.next(BracketToken.ROUND_R);
        lexer.next(BracketToken.CURLY_L);

        LinkedHashMap<List<ImmediateExpression>, List<JavaCode>> cases = new LinkedHashMap<>();
        List<JavaCode> defaultCase = null;
        while (!lexer.nextIf(BracketToken.CURLY_R)) {
            /* case conditions */
            List<ImmediateExpression> caseConditions = new ArrayList<>();
            boolean isDefault;
            if (lexer.nextIf(KeywordToken._case)) {
                do {
                    ImmediateToken immediate = (ImmediateToken) lexer.next(ImmediateToken.class);
                    caseConditions.add(new ImmediateExpression(immediate));
                } while (lexer.nextIf(SymbolToken.COMMA));
                isDefault = false;

            } else if (lexer.nextIf(KeywordToken._default)) {
                isDefault = true;

            } else {
                throw new JTAException.UnexpectedToken("case, default or '}'", lexer.next());
            }
            lexer.next(QuestColon.COLON);

            /* body */
            List<JavaCode> body = new ArrayList<>();
            while (!lexer.peek(KeywordToken._case)
                    && !lexer.peek(KeywordToken._default)
                    && !lexer.peek(BracketToken.CURLY_R)) {

                body.add(CodeParser.parseCode(lexer));
                JavaParser.eatSemiColons(lexer);
            }

            if (isDefault) {
                if (defaultCase != null) {
                    throw new JTAException("There can be only one default block");
                }
                defaultCase = body;
            } else {
                cases.put(caseConditions, body);
            }
        }

        return new JavaSwitch(condition, cases, defaultCase);
    }
}
