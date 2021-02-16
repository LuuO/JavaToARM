package javatoarm.parser;

import javatoarm.JTAException;
import javatoarm.javaast.JavaCode;
import javatoarm.javaast.JavaLambda;
import javatoarm.javaast.JavaLeftValue;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.expression.*;
import javatoarm.javaast.statement.JavaAssignment;
import javatoarm.javaast.statement.JavaFunctionCall;
import javatoarm.javaast.statement.JavaIncrementDecrement;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.*;
import javatoarm.token.operator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ExpressionParser {

    public static JavaExpression parse(JavaLexer lexer) throws JTAException {
        Stack<ExpressionElement> elements = new Stack<>();

        while (true) {
            Token token = lexer.next();

            // TODO: support condition casting, ternary
            if (token.equals(BracketToken.SQUARE_L)) {
                JavaExpression index = parse(lexer);
                lexer.next(BracketToken.SQUARE_R);
                if (elements.isEmpty() || elements.peek().expression() == null) {
                    throw new JTAException.InvalidOperation("Invalid array access");
                }
                JavaExpression array = ((Expression) elements.pop()).expression;
                addElement(elements, new JavaArrayElement(array, index));

            } else if (token instanceof MemberAccessToken) {
                if (elements.isEmpty() || elements.peek().expression() == null) {
                    throw new JTAException.InvalidOperation("Invalid member access");
                }
                JavaExpression left = elements.pop().expression();
                JavaName right = JavaParser.parseNamePath(lexer);
                addElement(elements, new MemberAccessExpression(left, right));

            } else if (token.equals(BracketToken.ROUND_L)) {
                if (!elements.isEmpty() && elements.peek().expression() instanceof JavaName) {
                    // Function call
                    lexer.rewind();
                    String name = elements.pop().expression().toString();
                    List<JavaRightValue> arguments = FunctionParser.parseCallArguments(lexer);
                    addElement(elements, new JavaFunctionCall(name, arguments));
                } else {
                    lexer.createCheckPoint();
                    JavaType type;
                    boolean isType;
                    try {
                        type = JavaParser.parseType(lexer, true);
                        isType = true;
                    } catch (JTAException e) {
                        isType = false;
                    }

                    if (lexer.nextIf(BracketToken.ROUND_R) && isType) {
                        // sub expression
                        lexer.deleteLastCheckPoint();
                    } else {
                        // sub expression
                        lexer.returnToLastCheckPoint();
                        JavaExpression expression = parse(lexer);
                        lexer.next(BracketToken.ROUND_R);
                        addElement(elements, expression);
                    }
                }
            } else if (token.equals(KeywordToken.NEW)) {
                lexer.rewind();
                JavaExpression rightValue = RightValueParser.parseNewInit(lexer);
                addElement(elements, rightValue);
            } else if (token instanceof OperatorToken) {
                addElement(elements, (OperatorToken) token);
            } else if (token instanceof ImmediateToken) {
                JavaImmediate constant = new JavaImmediate(((ImmediateToken) token));
                addElement(elements, constant);
            } else if (token instanceof NameToken || token.equals(KeywordToken.THIS)) {
                lexer.rewind();
                JavaName name = JavaParser.parseNamePath(lexer);
                addElement(elements, name);
            } else if (token.equals(new KeywordToken(KeywordToken.Keyword._instanceof))) {
                elements.add(new InstanceOf());
                elements.add(new Type(JavaParser.parseType(lexer, true)));
            } else {
                lexer.rewind();
                break;
            }
        }

        if (elements.size() == 0) {
            throw new JTAException.UnexpectedToken("expression", lexer.peek());
        }

        // TODO: improve performance
        parseIncrementDecrement(elements);
        parseUnaryExpression(elements);
        parseBinaryExpression(elements);
        parseTernaryToken(elements);
        parseAssignment(elements);

        if (elements.size() > 1) {
            ExpressionElement element = elements.get(1);
            if (element.expression() != null) {
                throw new JTAException.InvalidExpression(element.expression());
            } else {
                throw new JTAException.InvalidOperation(element.operator().toString());
            }
        }

        ExpressionElement result = elements.get(0);
        if (result.expression() == null) {
            throw new JTAException.InvalidOperation(result.operator().toString());
        }
        return result.expression();
    }

    private static void addElement(List<ExpressionElement> list, JavaExpression expression) {
        list.add(new Expression(expression));
    }

    private static void addElement(List<ExpressionElement> list, OperatorToken operator) {
        list.add(new Operator(operator));
    }

    private static void setElement(List<ExpressionElement> list, int index,
                                   JavaExpression expression) {
        if (index > list.size()) {
            throw new AssertionError();
        } else if (index == list.size()) {
            list.add(new Expression(expression));
        } else {
            list.set(index, new Expression(expression));
        }
    }

    private static void parseIncrementDecrement(List<ExpressionElement> elements)
            throws JTAException {

        for (int i = 0; i < elements.size(); i++) {
            OperatorToken operator = elements.get(i).operator();
            if (operator instanceof IncrementDecrement) {
                IncrementDecrement idOperator = (IncrementDecrement) operator;
                elements.remove(i);

                // TODO: check index, is variable
                if (i > 0 && elements.get(i - 1).expression() instanceof JavaName) {
                    i--;
                    JavaName variable = (JavaName) elements.get(i).expression();
                    JavaExpression expression = new JavaIncrementDecrement(
                            variable, true, idOperator.isIncrement);
                    setElement(elements, i, expression);

                } else if (i < elements.size() &&
                        elements.get(i).expression() instanceof JavaName) {
                    JavaName variable = (JavaName) elements.get(i).expression();
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
            OperatorToken operator = elements.get(i).operator();
            if (operator instanceof OperatorToken.Unary) {
                OperatorToken.Unary unaryOperator = (OperatorToken.Unary) operator;

                if (unaryOperator instanceof PlusMinus && i != 0
                        && elements.get(i - 1).expression() != null) {
                    continue;
                }

                // TODO: check index, condition
                JavaExpression operand = elements.remove(i + 1).expression();
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
        // TODO: implement shift?
        for (int level = 12; level >= 3; level--) {
            for (int i = 0; i < elements.size(); i++) {
                ExpressionElement current = elements.get(i);

                if (current.operator() instanceof OperatorToken.Binary) {
                    OperatorToken.Binary operator = (OperatorToken.Binary) current.operator();

                    if (operator.getPrecedenceLevel() == level) {
                        // TODO: check index
                        i--;
                        // TODO: check condition
                        JavaExpression operandLeft = elements.remove(i).expression();
                        elements.remove(i);
                        JavaExpression operandRight = elements.get(i).expression();
                        JavaExpression combined =
                                JavaExpression.newBinary(operator, operandLeft, operandRight);
                        setElement(elements, i, combined);

                    } else if (operator.getPrecedenceLevel() > level) {
                        throw new AssertionError();
                    }
                } else if (current instanceof InstanceOf && level == 9) {
                    i--;
                    JavaName valuePath = (JavaName) elements.remove(i).expression();
                    elements.remove(i);
                    JavaType targetType = elements.remove(i).type();
                    addElement(elements, (new InstanceOfExpression(valuePath, targetType)));
                }
            }
        }
    }

    private static void parseTernaryToken(Stack<ExpressionElement> elements) throws JTAException {
        for (int i = elements.size() - 1; i >= 0; i--) {
            OperatorToken operator = elements.get(i).operator();
            if (operator instanceof TernaryToken) {
                i -= 3;
                if (i < 0) {
                    throw new JTAException.InvalidOperation("missing left value");
                }
                JavaExpression condition = elements.remove(i).expression(); //TODO: check expression
                elements.remove(i); // ? sign
                JavaExpression trueExpression = elements.remove(i).expression();
                elements.remove(i); // : sign //TODO: check index
                JavaExpression falseExpression = elements.remove(i).expression();
                setElement(elements, i,
                        new TernaryExpression(condition, trueExpression, falseExpression));
            }
        }
    }

    private static void parseAssignment(List<ExpressionElement> elements) throws JTAException {
        for (int i = elements.size() - 1; i >= 0; i--) {
            OperatorToken operator = elements.get(i).operator();
            if (operator instanceof AssignmentOperator) {
                AssignmentOperator assignment = (AssignmentOperator) operator;
                i--;
                if (i < 0) {
                    throw new JTAException.InvalidOperation("missing left value");
                }
                JavaExpression leftExpression = elements.remove(i).expression();
                if (!(leftExpression instanceof JavaLeftValue)) {
                    throw new JTAException.InvalidOperation("not a left value");
                }
                JavaLeftValue leftValue = (JavaLeftValue) leftExpression;
                elements.remove(i);
                //TODO: check index
                JavaExpression value = elements.get(i).expression();

                if (assignment instanceof AssignmentOperator.Compound) {
                    OperatorToken.Binary implicit =
                            ((AssignmentOperator.Compound) assignment).implicitOperator;
                    value = new NumericExpression(implicit, leftExpression, value);
                }

                setElement(elements, i, new JavaAssignment(leftValue, value));
            }
        }
    }

    private interface ExpressionElement {
        default JavaExpression expression() {
            return null;
        }

        default OperatorToken operator() {
            return null;
        }

        default JavaType type() {
            return null;
        }
    }

    private static class Expression implements ExpressionElement {
        public final JavaExpression expression;

        public Expression(JavaExpression expression) {
            this.expression = expression;
        }

        @Override
        public JavaExpression expression() {
            return expression;
        }
    }

    private static class Operator implements ExpressionElement {
        public final OperatorToken operator;

        public Operator(OperatorToken operator) {
            this.operator = operator;
        }

        @Override
        public OperatorToken operator() {
            return operator;
        }
    }

    private static class InstanceOf implements ExpressionElement {
        public InstanceOf() {
        }
    }

    private static class Type implements ExpressionElement {
        public final JavaType type;

        public Type(JavaType type) {
            this.type = type;
        }

        @Override
        public JavaType type() {
            return type;
        }
    }

}
