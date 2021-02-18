package javatoarm.parser.expression;

import javatoarm.JTAException;

public class InvalidExpressionElement extends JTAException {
    public InvalidExpressionElement(ExpressionElement expressionElement) {
        super(expressionElement.toString());
    }
}
