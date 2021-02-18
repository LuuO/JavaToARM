package javatoarm.parser.expression;

import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.operator.OperatorToken;

interface ExpressionElement {
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

class Expression implements ExpressionElement {
    public final JavaExpression expression;

    public Expression(JavaExpression expression) {
        this.expression = expression;
    }

    @Override
    public JavaExpression expression() {
        return expression;
    }
}

class Operator implements ExpressionElement {
    public final OperatorToken operator;

    public Operator(OperatorToken operator) {
        this.operator = operator;
    }

    @Override
    public OperatorToken operator() {
        return operator;
    }
}

class InstanceOf implements ExpressionElement {
    public InstanceOf() {
    }
}

class Type implements ExpressionElement {
    public final JavaType type;

    public Type(JavaType type) {
        this.type = type;
    }

    @Override
    public JavaType type() {
        return type;
    }
}

class TypeCasting implements ExpressionElement {
    public final JavaType toType;

    public TypeCasting(JavaType toType) {
        this.toType = toType;
    }
}