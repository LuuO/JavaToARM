package javatoarm.parser.expression;

import javatoarm.JTAException;

/**
 * Exception for invalid elements in expressions
 */
public class InvalidExpressionElement extends JTAException {

    public InvalidExpressionElement(ExpressionElement expressionElement) {
        super(expressionElement.toString());
    }
}
