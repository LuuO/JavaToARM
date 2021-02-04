package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaBlock;
import javatoarm.java.JavaCode;
import javatoarm.java.JavaExpression;
import javatoarm.java.JavaFunctionCall;
import javatoarm.java.JavaNewArray;
import javatoarm.java.JavaRightValue;
import javatoarm.java.JavaStatement;
import javatoarm.java.JavaType;
import javatoarm.java.JavaVariableDeclare;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.StringToken;
import javatoarm.token.Token;
import javatoarm.token.operator.AssignmentOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class JavaParserCode {

    //TODO implement
    public static JavaCode parse(JavaLexer lexer) throws JTAException {
        Stack<JavaCode> stack = new Stack<>();

        do {
            Token token = lexer.next();
            if (token instanceof KeywordToken) {
                KeywordToken keywordToken = (KeywordToken) token;
                JavaType type = JavaType.get(keywordToken);
                if (keywordToken.keyword == KeywordToken.Keyword._for) {

                } else if (keywordToken.keyword == KeywordToken.Keyword._do) {

                } else {
                    stack.add(parseSingleStatement(lexer, Set.of(SplitterToken.SEMI_COLON)));
                }
            } else if (token instanceof StringToken) {

            } else if (token instanceof SplitterToken) {

            }

        } while (stack.size() != 1 && (stack.peek() instanceof JavaBlock));

        JavaCode result = stack.pop();
        if (!stack.isEmpty()) {
            throw new AssertionError();
        }

        return result;
    }

    /*
        Does not eat terminators
        TODO: Support int a, b, c; int a[], b, c;
     */
    private static JavaStatement parseSingleStatement(JavaLexer lexer, Set<Token> terminators)
        throws JTAException {

        if (lexer.peek() instanceof KeywordToken) {
            KeywordToken keywordToken = (KeywordToken) lexer.next();
            switch (keywordToken.keyword) {
                case _break:
                    return JavaStatement.BREAK;
                case _return:
                    if (terminators.contains(lexer.peek())) {
                        return new JavaStatement.Return();
                    } else {
                        return new JavaStatement.Return(ExpressionParser.parse(lexer));
                    }
                default:
                    throw new UnsupportedOperationException();
            }
        }


        if (isObjectCreation(lexer, terminators)) {
            /* If this is an object creation statement */
            JavaType type = JavaParser.parseType(lexer, true);
            String name = JavaParser.parseName(lexer);
            JavaRightValue initialValue = null;
            if (lexer.peek() instanceof AssignmentOperator.Simple) {
                lexer.next();
                if (lexer.peek().equals(new KeywordToken(KeywordToken.Keyword._new))) {
                    initialValue = parseNewInit(lexer);
                } else {
                    initialValue = ExpressionParser.parse(lexer);
                }
            }
            return new JavaVariableDeclare(new HashSet<>(), type, name, initialValue);
        } else {
            /* If this is not an object creation statement */
            JavaExpression expression = ExpressionParser.parse(lexer);
            //TODO check if the expression is a statement
            return null;
        }
    }

    /* Determine if this is an object creation statement. */
    private static boolean isObjectCreation(JavaLexer lexer, Set<Token> terminators) {

        if (lexer.peek() instanceof KeywordToken) {
            KeywordToken keywordToken = (KeywordToken) lexer.peek();
            return JavaType.get(keywordToken) != null;
        }

        lexer.createCheckPoint();
        for (int countString = 0; countString <= 2; ) {
            Token token = lexer.next();
            if (token instanceof StringToken) {
                countString++;
            } else if (token.equals(SplitterToken.COMMA)) {
                /* Commas only appear in variable declarations */
                lexer.createCheckPoint();
                return true;
            } else if (token instanceof AssignmentOperator) {
                lexer.createCheckPoint();
                return token instanceof AssignmentOperator.Simple && countString == 2;
            } else if (token.equals(BracketToken.SQUARE_L)) {
                /* array declaration */
                Token next = lexer.peek();
                lexer.createCheckPoint();
                return next.equals(BracketToken.SQUARE_R);
            } else if (terminators.contains(token)) {
                /* short statement: int a; */
                return true;
            } else {
                lexer.returnToLastCheckPoint();
                return false;
            }
        }
        lexer.returnToLastCheckPoint();
        return false;
    }

    private static JavaRightValue parseNewInit(JavaLexer lexer) throws JTAException {
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
            List<JavaExpression> arguments = parseFunctionCallArguments(lexer);
            lexer.next(BracketToken.ROUND_R);
            return new JavaFunctionCall(type, arguments);
        } else {
            throw new JTAException.UnexpectedToken("'[' or ')'", next);
        }
    }

    private static List<JavaExpression> parseFunctionCallArguments(JavaLexer lexer)
        throws JTAException {
        List<JavaExpression> arguments = new ArrayList<>();
        lexer.next(BracketToken.ROUND_L);
        if (!lexer.peek().equals(BracketToken.ROUND_R)) {
            for (; ; ) {
                arguments.add(ExpressionParser.parse(lexer));
                Token next = lexer.next();
                if (next.equals(BracketToken.ROUND_R)) {
                    break;
                } else if (!next.equals(SplitterToken.COMMA)) {
                    throw new JTAException.UnexpectedToken("',' or ')'", next);
                }
            }
        }
        return arguments;
    }
}

