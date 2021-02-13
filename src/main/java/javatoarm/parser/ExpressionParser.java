package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.java.JavaLeftValue;
import javatoarm.java.expression.JavaArrayElement;
import javatoarm.java.expression.JavaExpression;
import javatoarm.java.expression.JavaImmediate;
import javatoarm.java.expression.JavaName;
import javatoarm.java.expression.JavaUnaryExpression;
import javatoarm.java.expression.NumericExpression;
import javatoarm.java.statement.JavaAssignment;
import javatoarm.java.statement.JavaFunctionCall;
import javatoarm.java.statement.JavaIncrementDecrement;
import javatoarm.token.BracketToken;
import javatoarm.token.ImmediateToken;
import javatoarm.token.JavaLexer;
import javatoarm.token.NameToken;
import javatoarm.token.Token;
import javatoarm.token.operator.AssignmentOperator;
import javatoarm.token.operator.IncrementDecrement;
import javatoarm.token.operator.OperatorToken;
import javatoarm.token.operator.PlusMinus;

import java.util.List;
import java.util.Stack;

public class ExpressionParser {

    public static JavaExpression parse(JavaLexer lexer) throws JTAException {
        Stack<ExpressionElement> elements = new Stack<>();

        while (true) {
            Token token = lexer.next();

            // TODO: support condition casting, new Object creation, ternary
            if (token.equals(BracketToken.SQUARE_L)) {
                JavaExpression index = parse(lexer);
                lexer.next(BracketToken.SQUARE_R);
                if (elements.isEmpty() || elements.peek().expression == null) {
                    throw new JTAException.InvalidOperation("Invalid array access");
                }
                JavaExpression array = elements.pop().expression;
                addElement(elements, new JavaArrayElement(array, index));

            } else if (token.equals(BracketToken.ROUND_L)) {
                if (elements.peek().expression instanceof JavaName) {
                    // Function call
                    lexer.rewind();
                    String name = elements.pop().expression.toString();
                    List<JavaExpression> arguments = FunctionParser.parseCallArguments(lexer);
                    addElement(elements, new JavaFunctionCall(name, arguments));
                } else {
                    // sub expression
                    JavaExpression expression = parse(lexer);
                    lexer.next(BracketToken.ROUND_R);
                    addElement(elements, expression);
                }

            } else if (token instanceof OperatorToken) {
                addElement(elements, (OperatorToken) token);
            } else if (token instanceof ImmediateToken) {
                JavaImmediate constant = new JavaImmediate(((ImmediateToken) token));
                addElement(elements, constant);
            } else if (token instanceof NameToken) {
                lexer.rewind();
                JavaName name = JavaParser.parseNamePath(lexer);
                addElement(elements, name);
            } else {
                lexer.rewind();
                break;
            }
        }

        if (elements.size() == 0) {
            throw new JTAException.UnexpectedToken("expression", lexer.peek());
        }

        // TODO: performance
        parseIncrementDecrement(elements);
        parseUnaryExpression(elements);
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
                if (i > 0 && elements.get(i - 1).expression instanceof JavaName) {
                    i--;
                    JavaName variable = (JavaName) elements.get(i).expression;
                    JavaExpression expression = new JavaIncrementDecrement(
                        variable, true, idOperator.isIncrement);
                    setElement(elements, i, expression);

                } else if (i < elements.size() &&
                    elements.get(i).expression instanceof JavaName) {
                    JavaName variable = (JavaName) elements.get(i).expression;
                    JavaExpression expression = new JavaIncrementDecrement(
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
                    && elements.get(i - 1).expression != null) {
                    continue;
                }

                // TODO: check index, condition
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
                        // TODO: check condition
                        JavaExpression operandLeft = elements.remove(i).expression;
                        elements.remove(i);
                        JavaExpression operandRight = elements.get(i).expression;
                        JavaExpression combined =
                            JavaExpression.newBinary(operator, operandLeft, operandRight);
                        setElement(elements, i, combined);

                    } else if (operator.getPrecedenceLevel() > level) {
                        throw new AssertionError();
                    }
                }
            }
        }
    }

    private static void parseAssignment(List<ExpressionElement> elements) throws JTAException {
        for (int i = elements.size() - 1; i >= 0; i--) {
            OperatorToken operator = elements.get(i).operator;
            if (operator instanceof AssignmentOperator) {
                AssignmentOperator assignment = (AssignmentOperator) operator;
                i--;
                if (i < 0) {
                    throw new JTAException.InvalidOperation("missing left value");
                }
                JavaExpression leftExpression = elements.remove(i).expression;
                if (!(leftExpression instanceof JavaLeftValue)) {
                    throw new JTAException.InvalidOperation("not a left value");
                }
                JavaLeftValue leftValue = (JavaLeftValue) leftExpression;
                elements.remove(i);
                //TODO: check index
                JavaExpression value = elements.get(i).expression;

                if (assignment instanceof AssignmentOperator.Compound) {
                    OperatorToken.Binary implicit =
                        ((AssignmentOperator.Compound) assignment).implicitOperator;
                    value = new NumericExpression(implicit, leftExpression, value);
                }

                setElement(elements, i, new JavaAssignment(leftValue, value));
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
