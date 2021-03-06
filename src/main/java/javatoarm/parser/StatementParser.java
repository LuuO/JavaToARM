package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaProperty;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.expression.NewObjectExpression;
import javatoarm.javaast.statement.JavaFunctionCall;
import javatoarm.javaast.statement.JavaStatement;
import javatoarm.javaast.statement.ThrowStatement;
import javatoarm.javaast.statement.VariableDeclareStatement;
import javatoarm.javaast.type.ArrayType;
import javatoarm.javaast.type.JavaType;
import javatoarm.javaast.type.PrimitiveType;
import javatoarm.parser.expression.ExpressionParser;
import javatoarm.token.*;
import javatoarm.token.operator.AssignmentOperator;

import java.util.List;
import java.util.Set;

public class StatementParser {

    /**
     * Parse a statement. It will not eat the terminating token.
     *
     * @param lexer the lexer
     * @return the next statement
     * @throws JTAException if an error occurs
     */
    public static JavaStatement parse(JavaLexer lexer)
            throws JTAException {
        // TODO: Support int a, b, c; int a[], b, c;

        /* branch statement */
        Token next = lexer.peek();
        if (next instanceof KeywordToken) {
            KeywordToken keyword = (KeywordToken) next;
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
                    if (lexer.peek(SymbolToken.SEMI_COLON)) {
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
                    if (lexer.peek(BracketToken.ROUND_L)) {
                        List<JavaRightValue> arguments = FunctionParser.parseCallArguments(lexer);
                        return new JavaFunctionCall("this", arguments);
                    }
                    lexer.rewind();
                }
            }
        }

        /* Not control statement */
        if (isObjectCreation(lexer)) {
            /* object creation */
            Set<JavaProperty> properties = lexer.nextIf(KeywordToken._final)
                    ? Set.of(JavaProperty.FINAL) : Set.of();

            JavaType type = TypeParser.parseType(lexer, true);
            String name = JavaParser.parseName(lexer);

            if (lexer.nextIf(BracketToken.SQUARE_L)) {
                lexer.next(BracketToken.SQUARE_R);
                type = new ArrayType(type);
            }

            JavaRightValue initialValue = null;
            if (lexer.nextIf(AssignmentOperator.Simple.INSTANCE)) {
                if (lexer.peek(KeywordToken._new)) {
                    initialValue = RightValueParser.parseNewInit(lexer);
                } else {
                    initialValue = ExpressionParser.parse(lexer);
                }
            }
            return new VariableDeclareStatement(type, name, initialValue, properties);

        } else {
            /* expression */
            JavaExpression expression = ExpressionParser.parse(lexer);
            if (expression instanceof JavaStatement) {
                return (JavaStatement) expression;
            } else {
                throw new JTAException.NotAStatement();
            }
        }
    }

    /**
     * Determine if the following is an object creation statement.
     *
     * @param lexer the lexer
     * @return true if the following is an object creation statement, false otherwise
     * @throws JTAException if error occurs
     */
    private static boolean isObjectCreation(JavaLexer lexer) throws JTAException {

        Token peek = lexer.peek();
        if (peek instanceof KeywordToken) {
            if (peek.equals(KeywordToken._final)) {
                return true;
            }
            return PrimitiveType.get((KeywordToken) peek) != null;
        }

        lexer.createCheckPoint();
        for (int countString = 0; countString <= 2; ) {
            Token token = lexer.next();
            if (token instanceof NameToken) {
                lexer.rewind();
                JavaParser.parseMemberPath(lexer);
                countString++;

            } else if (token.equals(AngleToken.LEFT)) {
                lexer.returnToLastCheckPoint();
                return true;

            } else if (token.equals(SymbolToken.COMMA)) {
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

            } else if (token.equals(SymbolToken.SEMI_COLON)) {
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
