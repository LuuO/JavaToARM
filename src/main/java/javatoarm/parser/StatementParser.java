package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.*;
import javatoarm.token.*;
import javatoarm.token.operator.AssignmentOperator;

import java.util.HashSet;
import java.util.List;

public class StatementParser {

    /*
        Does not eat terminators
        TODO: Support int a, b, c; int a[], b, c;
     */
    public static JavaStatement parse(JavaLexer lexer)
            throws JTAException {

        /* control statement */
        Token next = lexer.peek();
        if (next instanceof KeywordToken) {
            KeywordToken.Keyword keyword = ((KeywordToken) next).keyword;
            switch (keyword) {
                case _break -> {
                    lexer.next();
                    return JavaStatement.BREAK;
                }
                case _return -> {
                    lexer.next();
                    if (lexer.peek().equals(SplitterToken.SEMI_COLON)) {
                        return new JavaStatement.Return();
                    } else {
                        return new JavaStatement.Return(ExpressionParser.parse(lexer));
                    }
                }
            }
        }

        /* object creation or expression */
        if (isObjectCreation(lexer)) {
            JavaType type = JavaParser.parseType(lexer, true);
            String name = JavaParser.parseSimpleName(lexer);
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
            JavaExpression expression = ExpressionParser.parse(lexer);
            if (expression instanceof JavaStatement) {
                return (JavaStatement) expression;
            } else {
                throw new JTAException.NotAStatement();
            }
        }
    }

    /* Determine if this is an object creation statement. */
    private static boolean isObjectCreation(JavaLexer lexer) {

        if (lexer.peek() instanceof KeywordToken) {
            KeywordToken keywordToken = (KeywordToken) lexer.peek();
            return JavaType.get(keywordToken) != null;
        }

        lexer.createCheckPoint();
        for (int countString = 0; countString <= 2; ) {
            Token token = lexer.next();
            if (token instanceof NameToken) {
                countString++;
            } else if (token.equals(SplitterToken.COMMA)) {
                /* Commas only appear in variable declarations */
                lexer.returnToLastCheckPoint();
                return true;

            } else if (token instanceof AssignmentOperator) {
                lexer.returnToLastCheckPoint();
                return token instanceof AssignmentOperator.Simple && countString == 2;

            } else if (token.equals(BracketToken.SQUARE_L)) {
                /* array declaration */
                Token next = lexer.peek();
                lexer.returnToLastCheckPoint();
                return next.equals(BracketToken.SQUARE_R);

            } else if (token.equals(SplitterToken.SEMI_COLON)) {
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
            List<JavaExpression> arguments = FunctionParser.parseCallArguments(lexer);
            lexer.next(BracketToken.ROUND_R);
            return new JavaFunctionCall(type, arguments);

        } else {
            throw new JTAException.UnexpectedToken("'[' or ')'", next);
        }
    }
}
