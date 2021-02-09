package javatoarm.java;

import javatoarm.token.operator.OperatorToken;

public class BinaryExpression implements JavaExpression {
    OperatorToken.Binary operator;
    JavaExpression operandLeft, operandRight;

    public BinaryExpression(OperatorToken.Binary operator, JavaExpression operandLeft, JavaExpression operandRight) {
        this.operator = operator;
        this.operandLeft = operandLeft;
        this.operandRight = operandRight;
    }
}
