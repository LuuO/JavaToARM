package javatoarm.parser.expression;

import javatoarm.JTAException;
import javatoarm.javaast.JavaLeftValue;
import javatoarm.javaast.JavaRightValue;
import javatoarm.javaast.expression.*;
import javatoarm.javaast.statement.JavaAssignment;
import javatoarm.javaast.statement.JavaFunctionCall;
import javatoarm.javaast.statement.JavaIncrementDecrement;
import javatoarm.javaast.type.JavaType;
import javatoarm.parser.FunctionParser;
import javatoarm.parser.JavaParser;
import javatoarm.parser.RightValueParser;
import javatoarm.parser.TypeParser;
import javatoarm.token.*;
import javatoarm.token.operator.*;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ExpressionParser {

    /**
     * Parse an expression
     *
     * @param lexer the lexer
     * @return an expression
     * @throws JTAException if an error occurs
     */
    public static JavaExpression parse(JavaLexer lexer) throws JTAException {
        if (LambdaParser.isLambda(lexer)) {
            return LambdaParser.parse(lexer);
        }

        /* collect all elements in the expression */
        List<ExpressionElement> elements = parseElementList(lexer);
        if (elements.size() == 0) {
            throw new JTAException.UnexpectedToken("expression", lexer.peek());
        }

        /* analyze expression elements */
        parseIncrementDecrement(elements);
        parseUnaryOperations(elements);
        parseTypeCasting(elements);
        parseBinaryExpression(elements);
        parseTernaryExpression(elements);
        parseAssignment(elements);

        if (elements.size() > 1) {
            throw new InvalidExpressionElement(elements.get(1));
        }

        ExpressionElement result = elements.get(0);
        if (result.expression() == null) {
            throw new InvalidExpressionElement(result);
        }
        return result.expression();
    }

    /**
     * Collect all expression elements
     *
     * @param lexer the lexer
     * @return a list of expression elements
     * @throws JTAException if an error occurs
     */
    private static List<ExpressionElement> parseElementList(JavaLexer lexer) throws JTAException {
        LinkedList<ExpressionElement> elements = new LinkedList<>();

        while (true) {
            Token next = lexer.next();

            if (next.equals(BracketToken.SQUARE_L)) {
                /* access array element */
                if (elements.isEmpty() || elements.peekLast().expression() == null) {
                    throw new JTAException.InvalidOperation("Missing array");
                }
                JavaExpression index = parse(lexer);
                lexer.next(BracketToken.SQUARE_R);

                JavaExpression array = elements.removeLast().expression();
                addElement(elements, new JavaArrayElement(array, index));

            } else if (next.equals(SymbolToken.DOT)) {
                if (elements.isEmpty() || elements.peekLast().expression() == null) {
                    throw new JTAException.InvalidOperation("Invalid member access");
                }
                JavaExpression left = elements.removeLast().expression();
                JavaMember right = JavaParser.parseMemberPath(lexer);
                addElement(elements, new JavaMemberAccess(left, right));

            } else if (next.equals(BracketToken.ROUND_L)) {
                if (!elements.isEmpty() && elements.peekLast().expression() instanceof JavaMember) {
                    /* Function call */
                    lexer.rewind();
                    String functionPath = elements.removeLast().expression().toString();
                    List<JavaRightValue> arguments = FunctionParser.parseCallArguments(lexer);
                    addElement(elements, new JavaFunctionCall(functionPath, arguments));

                } else {
                    /* type casting or sub expression */
                    lexer.createCheckPoint();
                    JavaType type;
                    try {
                        type = TypeParser.parseType(lexer, true);
                    } catch (JTAException ignored) {
                        type = null;
                    }

                    if (lexer.nextIf(BracketToken.ROUND_R) && type != null) {
                        /* type casting */
                        lexer.deleteLastCheckPoint();
                        elements.add(new TypeCasting(type));
                    } else {
                        /* sub expression */
                        lexer.returnToLastCheckPoint();
                        JavaExpression expression = parse(lexer);
                        lexer.next(BracketToken.ROUND_R);
                        addElement(elements, expression);
                    }
                }

            } else if (next.equals(KeywordToken._new)) {
                lexer.rewind();
                JavaExpression rightValue = RightValueParser.parseNewInit(lexer);
                addElement(elements, rightValue);

            } else if (next instanceof OperatorToken) {
                addElement(elements, (OperatorToken) next);

            } else if (next instanceof ImmediateToken) {
                ImmediateExpression constant = new ImmediateExpression(((ImmediateToken) next));
                addElement(elements, constant);

            } else if (next instanceof NameToken || next.equals(KeywordToken._this)) {
                lexer.rewind();
                JavaMember member = JavaParser.parseMemberPath(lexer);
                addElement(elements, member);

            } else if (next.equals(KeywordToken._instanceof)) {
                elements.add(new InstanceOf());
                elements.add(new Type(TypeParser.parseType(lexer, true)));

            } else {
                lexer.rewind();
                break;
            }
        }
        return elements;
    }

    /**
     * Parse all increment and decrement expressions in the list.
     * Example: i++, --i
     *
     * @param elements the element list to modify
     * @throws JTAException if an error occurs
     */
    private static void parseIncrementDecrement(List<ExpressionElement> elements)
            throws JTAException {

        ListIterator<ExpressionElement> iterator = elements.listIterator();
        while (iterator.hasNext()) {
            OperatorToken operator = iterator.next().operator();
            if (operator instanceof IncrementDecrement) {
                boolean increment = operator == IncrementDecrement.INCREMENT;
                iterator.remove();

                if (iterator.hasPrevious()) {
                    JavaExpression previous = iterator.previous().expression();
                    if (previous instanceof JavaMember) {
                        JavaExpression expression =
                                new JavaIncrementDecrement((JavaMember) previous, true, increment);
                        iterator.set(new Expression(expression));
                        continue;
                    }
                }

                if (iterator.hasNext()) {
                    JavaExpression next = iterator.next().expression();
                    if (next instanceof JavaMember) {
                        JavaExpression expression =
                                new JavaIncrementDecrement((JavaMember) next, false, increment);
                        iterator.set(new Expression(expression));
                        continue;
                    }
                }

                throw new JTAException.InvalidOperation(operator.toString());
            }
        }

    }

    /**
     * Parse all unary operation expressions in the list except increment and decrement expressions.
     * Example: +a, -2, !true
     *
     * @param elements the element list to modify
     * @throws JTAException if an error occurs
     */
    private static void parseUnaryOperations(List<ExpressionElement> elements) throws JTAException {
        ListIterator<ExpressionElement> iterator = elements.listIterator();
        while (iterator.hasNext()) {
            OperatorToken operator = iterator.next().operator();
            if (operator instanceof OperatorToken.Unary) {
                OperatorToken.Unary unaryOperator = (OperatorToken.Unary) operator;

                /* if it is +/-, check whether it is unary or binary */
                if (unaryOperator instanceof PlusMinus && iterator.previousIndex() > 0) {
                    iterator.previous();
                    ExpressionElement elementBeforeSign = iterator.previous();
                    iterator.next();
                    iterator.next();
                    if (elementBeforeSign.expression() != null) {
                        continue;
                    }
                }

                iterator.remove();
                JavaExpression operand = getNextOperandExpression(iterator);
                iterator.set(new Expression(new UnaryExpression(unaryOperator, operand)));
            }
        }
    }

    /**
     * Parse all type casting expressions in the list.
     * Example: (long) i, (Subtype) Object
     *
     * @param elements the element list to modify
     * @throws JTAException if an error occurs
     */
    private static void parseTypeCasting(List<ExpressionElement> elements) throws JTAException {
        ListIterator<ExpressionElement> iterator = elements.listIterator();
        while (iterator.hasNext()) {
            ExpressionElement current = iterator.next();
            if (current instanceof TypeCasting) {
                JavaType toType = ((TypeCasting) current).toType;
                iterator.remove();

                JavaExpression operand = getNextOperandExpression(iterator);

                iterator.set(new Expression(new TypeCastingExpression(toType, operand)));
            }
        }
    }

    /**
     * Parse all binary operation expressions in the list.
     * Example: 1 + 2, 3 << 5, 2 == 9, true || false
     *
     * @param elements the element list to modify
     * @throws JTAException if an error occurs
     */
    private static void parseBinaryExpression(List<ExpressionElement> elements) throws JTAException {
        for (int level = 12; level >= 3; level--) {
            ListIterator<ExpressionElement> iterator = elements.listIterator();
            while (iterator.hasNext()) {
                ExpressionElement current = iterator.next();

                if (level == 9 && current instanceof InstanceOf) {
                    iterator.remove();
                    JavaExpression memberPath = getPreviousOperandExpression(iterator);
                    iterator.remove();
                    JavaType targetType = iterator.next().type();
                    if (targetType == null) {
                        throw new JTAException.InvalidOperation("Invalid right operand");
                    }
                    iterator.set(new Expression(new InstanceOfExpression(memberPath, targetType)));

                } else if (current.operator() instanceof OperatorToken.Binary) {
                    OperatorToken.Binary binaryOperator = (OperatorToken.Binary) current.operator();

                    if (binaryOperator.getPrecedenceLevel() == level) {
                        iterator.remove();
                        JavaExpression operandLeft = getPreviousOperandExpression(iterator);
                        iterator.remove();
                        JavaExpression operandRight = getNextOperandExpression(iterator);
                        JavaExpression combined =
                                JavaExpression.newBinary(binaryOperator, operandLeft, operandRight);
                        iterator.set(new Expression(combined));

                    } else if (binaryOperator.getPrecedenceLevel() > level) {
                        throw new AssertionError();
                    }

                }
            }
        }
    }

    /**
     * Parse all ternary expressions in the list.
     * Example: true ? 1 : false
     *
     * @param elements the element list to modify
     * @throws JTAException if an error occurs
     */
    private static void parseTernaryExpression(List<ExpressionElement> elements) throws JTAException {
        ListIterator<ExpressionElement> iterator = elements.listIterator(elements.size());
        while (iterator.hasPrevious()) {
            if (QuestColon.COLON.equals(iterator.previous().operator())) {
                iterator.remove();
                JavaExpression falseExpression = getNextOperandExpression(iterator);
                iterator.remove();
                JavaExpression trueExpression = getPreviousOperandExpression(iterator);
                iterator.remove();
                if (!iterator.hasPrevious() || !QuestColon.QUESTION.equals(iterator.previous().operator())) {
                    throw new JTAException.InvalidOperation("Incomplete ternary expression");
                }
                iterator.remove();

                JavaExpression condition = getPreviousOperandExpression(iterator);
                iterator.set(new Expression(new TernaryExpression(condition, trueExpression, falseExpression)));
            }
        }
    }

    /**
     * Parse all assignment expressions in the list.
     * Example: i = 0, (i += 1)
     *
     * @param elements the element list to modify
     * @throws JTAException if an error occurs
     */
    private static void parseAssignment(List<ExpressionElement> elements) throws JTAException {
        ListIterator<ExpressionElement> iterator = elements.listIterator(elements.size());
        while (iterator.hasPrevious()) {
            OperatorToken operator = iterator.previous().operator();
            if (operator instanceof AssignmentOperator) {
                AssignmentOperator assignment = (AssignmentOperator) operator;
                iterator.remove();

                JavaExpression leftExpression = getPreviousOperandExpression(iterator);
                iterator.remove();

                if (!(leftExpression instanceof JavaLeftValue)) {
                    throw new JTAException.InvalidOperation("Not a valid left value");
                }
                JavaLeftValue leftValue = (JavaLeftValue) leftExpression;

                JavaExpression rightValue = getNextOperandExpression(iterator);

                if (assignment instanceof AssignmentOperator.Compound) {
                    OperatorToken.Binary implicit =
                            ((AssignmentOperator.Compound) assignment).implicitOperator;
                    rightValue = new NumericExpression(implicit, leftExpression, rightValue);
                }

                iterator.set(new Expression(new JavaAssignment(leftValue, rightValue)));
            }
        }

    }

    /**
     * Add an expression to the element list
     *
     * @param list       the element list
     * @param expression the expression
     */
    private static void addElement(List<ExpressionElement> list, JavaExpression expression) {
        list.add(new Expression(expression));
    }

    /**
     * Add an operator to the element list
     *
     * @param list     the element list
     * @param operator the operator
     */
    private static void addElement(List<ExpressionElement> list, OperatorToken operator) {
        list.add(new Operator(operator));
    }

    /**
     * Get previous expression in the element list.
     * <p>
     * This method checks (1) if the iterator has previous element; (2) if previous element is an expression.
     * </p>
     *
     * @param iterator the list iterator of the element list
     * @return previous expression
     * @throws JTAException an exception will be thrown when appropriate
     */
    private static JavaExpression getPreviousOperandExpression(ListIterator<ExpressionElement> iterator)
            throws JTAException {

        if (!iterator.hasPrevious()) {
            throw new JTAException.InvalidOperation("Operation missing left operand");
        }

        JavaExpression operandLeft = iterator.previous().expression();
        if (operandLeft == null) {
            throw new JTAException.InvalidOperation("Invalid left operand");
        }

        return operandLeft;
    }

    /**
     * Get next expression in the element list.
     * <p>
     * This method checks (1) if the iterator has next element; (2) if next element is an expression.
     * </p>
     *
     * @param iterator the list iterator of the element list
     * @return next expression
     * @throws JTAException an exception will be thrown when appropriate
     */
    private static JavaExpression getNextOperandExpression(ListIterator<ExpressionElement> iterator)
            throws JTAException {

        if (!iterator.hasNext()) {
            throw new JTAException.InvalidOperation("Operation missing right operand");
        }

        JavaExpression operandRight = iterator.next().expression();
        if (operandRight == null) {
            throw new JTAException.InvalidOperation("Invalid right operand");
        }

        return operandRight;
    }

}
