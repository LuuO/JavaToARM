package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.ArrayAccessExpression;
import javatoarm.java.BinaryExpression;
import javatoarm.java.JavaAssignment;
import javatoarm.java.JavaImmediate;
import javatoarm.java.JavaExpression;
import javatoarm.java.JavaIncrementDecrementExpression;
import javatoarm.java.JavaUnaryExpression;
import javatoarm.java.JavaVariable;
import javatoarm.token.BracketToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.Token;
import javatoarm.token.ValueToken;
import javatoarm.token.operator.AssignmentOperator;
import javatoarm.token.operator.IncrementDecrement;
import javatoarm.token.operator.OperatorToken;
import javatoarm.token.operator.PlusMinus;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParser {

    public static JavaExpression parse(JavaLexer lexer) throws JTAException {
        ArrayList<ExpressionElement> elements = new ArrayList<>();

        while (true) {
            Token token = lexer.next();

            // TODO: support type casting, object creation
            if (token.equals(BracketToken.SQUARE_L)) {
                JavaExpression expression = parse(lexer);
                lexer.next(BracketToken.SQUARE_R);
                addElement(elements, new ArrayAccessExpression(expression));
            } else if (token.equals(BracketToken.ROUND_L)) {
                JavaExpression expression = parse(lexer);
                lexer.next(BracketToken.ROUND_R);
                addElement(elements, expression);
            } else if (token instanceof OperatorToken) {
                addElement(elements, (OperatorToken) token);
            } else if (token instanceof ValueToken) {
                JavaImmediate constant = new JavaImmediate(((ValueToken) token));
                addElement(elements, constant);
            } else {
                lexer.rewind();
                break;
            }
        }

        // TODO: support function calls and array indexing and new initialization
        // TODO: performance
        parseIncrementDecrement(elements);
        parseUnaryExpression(elements);

        // TODO: shift, relational, ternary
        parseBinaryExpression(elements);
        parseAssignment(elements);

        if (elements.size() > 1) {
            ExpressionElement element = elements.get(1);
            if (element.expression != null) {
                throw new JTAException.InvalidExpression(element.expression);
            } else {
                throw new JTAException.InvalidOperation(element.operator.toString());
            }
        }

        ExpressionElement result = elements.get(0);
        if (result.expression == null) {
            throw new JTAException.InvalidOperation(result.operator.toString());
        }
        return result.expression;
    }

    private static void addElement(List<ExpressionElement> list, JavaExpression expression) {
        list.add(new ExpressionElement(expression));
    }

    private static void addElement(List<ExpressionElement> list, OperatorToken operator) {
        list.add(new ExpressionElement(operator));
    }

    private static void setElement(List<ExpressionElement> list, int index,
                                   JavaExpression expression) {
        list.set(index, new ExpressionElement(expression));
    }

    private static void parseIncrementDecrement(List<ExpressionElement> elements)
        throws JTAException {

        for (int i = 0; i < elements.size(); i++) {
            OperatorToken operator = elements.get(i).operator;
            if (operator instanceof IncrementDecrement) {
                IncrementDecrement idOperator = (IncrementDecrement) operator;
                elements.remove(i);

                // TODO: check index, is variable
                if (i > 0 && elements.get(i - 1).expression instanceof JavaVariable) {
                    i--;
                    JavaVariable variable = (JavaVariable) elements.get(i).expression;
                    JavaExpression expression = new JavaIncrementDecrementExpression(
                        variable, true, idOperator.isIncrement);
                    setElement(elements, i, expression);

                } else if (i < elements.size() &&
                    elements.get(i).expression instanceof JavaVariable) {
                    JavaVariable variable = (JavaVariable) elements.get(i).expression;
                    JavaExpression expression = new JavaIncrementDecrementExpression(
                        variable, false, idOperator.isIncrement);
                    setElement(elements, i, expression);

                } else {
                    throw new JTAException.InvalidOperation(idOperator.toString());
                }
            }
        }
    }

    private static void parseUnaryExpression(List<ExpressionElement> elements) {
        for (int i = 0; i < elements.size(); i++) {
            OperatorToken operator = elements.get(i).operator;
            if (operator instanceof OperatorToken.Unary) {
                OperatorToken.Unary unaryOperator = (OperatorToken.Unary) operator;

                if (unaryOperator instanceof PlusMinus && i != 0
                    && elements.get(i - 1).expression instanceof JavaVariable) {
                    continue;
                }

                // TODO: check index, type
                JavaExpression operand = elements.remove(i + 1).expression;
                setElement(elements, i, new JavaUnaryExpression(unaryOperator, operand));
            }
        }
    }

    /**
     * level 12 - 3
     *
     * @param elements
     */
    private static void parseBinaryExpression(List<ExpressionElement> elements) {
        // TODO: implement shift
        for (int level = 12; level >= 3; level--) {
            for (int i = 0; i < elements.size(); i++) {
                ExpressionElement current = elements.get(i);

                if (current.operator != null) {
                    OperatorToken.Binary operator = (OperatorToken.Binary) current.operator;

                    if (operator.getPrecedenceLevel() == level) {
                        // TODO: check index
                        i--;
                        // TODO: check type
                        JavaExpression operandLeft = elements.remove(i).expression;
                        elements.remove(i);
                        JavaExpression operandRight = elements.get(i).expression;
                        setElement(elements, i,
                            new BinaryExpression(operator, operandLeft, operandRight));

                    } else if (operator.getPrecedenceLevel() > level) {
                        throw new AssertionError();
                    }
                }
            }
        }
    }

    private static void parseAssignment(List<ExpressionElement> elements) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            OperatorToken operator = elements.get(i).operator;
            if (operator instanceof AssignmentOperator) {
                AssignmentOperator assignment = (AssignmentOperator) operator;
                // TODO: check index
                i--;
                // TODO: check type
                JavaVariable variable = (JavaVariable) elements.remove(i).expression;
                elements.remove(i);
                JavaExpression value = elements.get(i).expression;

                if (assignment instanceof AssignmentOperator.Compound) {
                    OperatorToken.Binary implicit = ((AssignmentOperator.Compound) assignment).implicitOperator;
                    value = new BinaryExpression(implicit, variable, value);
                }

                setElement(elements, i, new JavaAssignment(variable, value));
            }
        }
    }

    private static class ExpressionElement {
        public final OperatorToken operator;
        public final JavaExpression expression;

        public ExpressionElement(OperatorToken operator) {
            this.operator = operator;
            this.expression = null;
        }

        public ExpressionElement(JavaExpression expression) {
            this.operator = null;
            this.expression = expression;
        }
    }
}
