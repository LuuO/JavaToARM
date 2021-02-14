package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaRightValue;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.expression.NewObjectExpression;
import javatoarm.java.statement.JavaFunctionCall;
import javatoarm.java.statement.JavaStatement;
import javatoarm.java.statement.JavaVariableDeclare;
import javatoarm.java.statement.ThrowStatement;
import javatoarm.java.type.JavaSimpleType;
import javatoarm.java.type.JavaType;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.KeywordToken;
import javatoarm.token.NameToken;
import javatoarm.token.SplitterToken;
import javatoarm.token.Token;
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
                    return new JavaStatement.Break();
                }
                case _continue -> {
                    lexer.next();
                    return new JavaStatement.Continue();
                }
                case _return -> {
                    lexer.next();
                    if (lexer.peek().equals(SplitterToken.SEMI_COLON)) {
                        return new JavaStatement.Return();
                    } else {
                        return new JavaStatement.Return(ExpressionParser.parse(lexer));
                    }
                }
                case _throw -> {
                    lexer.next();
                    JavaExpression expression = ExpressionParser.parse(lexer);
                    if (!(expression instanceof NewObjectExpression)) {
                        throw new JTAException.InvalidExpression(expression);
                    }
                    return new ThrowStatement((NewObjectExpression) expression);
                }
                case _this -> {
                    lexer.next();
                    if (lexer.peek().equals(BracketToken.ROUND_L)) {
                        List<JavaExpression> arguments = FunctionParser.parseCallArguments(lexer);
                        return new JavaFunctionCall("this", arguments);
                    }
                    lexer.rewind();
                }
            }
        }

        /* object creation or expression */
        if (isObjectCreation(lexer)) {
            JavaType condition = JavaParser.parseType(lexer, true);
            String name = JavaParser.parseSimpleName(lexer);
            JavaRightValue initialValue = null;
            if (lexer.peek() instanceof AssignmentOperator.Simple) {
                lexer.next();
                if (lexer.peek().equals(new KeywordToken(KeywordToken.Keyword._new))) {
                    initialValue = RightValueParser.parseNewInit(lexer);
                } else {
                    initialValue = ExpressionParser.parse(lexer);
                }
            }
            return new JavaVariableDeclare(new HashSet<>(), condition, name, initialValue);

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
    private static boolean isObjectCreation(JavaLexer lexer) throws JTAException {

        if (lexer.peek() instanceof KeywordToken) {
            KeywordToken keywordToken = (KeywordToken) lexer.peek();
            return JavaSimpleType.get(keywordToken) != null;
        }

        lexer.createCheckPoint();
        for (int countString = 0; countString <= 2; ) {
            Token token = lexer.next();
            if (token instanceof NameToken) {
                lexer.rewind();
                JavaParser.parseNamePath(lexer);
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

}
