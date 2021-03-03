package javatoarm.parser.expression;

import javatoarm.javaast.expression.JavaExpression;
import javatoarm.javaast.type.JavaType;
import javatoarm.token.operator.OperatorToken;

/**
 * Containers for expression elements
 */
interface ExpressionElement {
    /**
     * Get the expression
     *
     * @return if this element is an expression, returns the expression, otherwise returns null.
     */
    default JavaExpression expression() {
        return null;
    }

    /**
     * Get the operator
     *
     * @return if this element is an operator, returns the operator, otherwise returns null.
     */
    default OperatorToken operator() {
        return null;
    }

    /**
     * Get the type
     *
     * @return if this element is a type, returns the type, otherwise returns null.
     */
    default JavaType type() {
        return null;
    }
}

/**
 * An expression
 */
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

/**
 * An operator
 */
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

/**
 * An instanceof operator
 */
class InstanceOf implements ExpressionElement {
    public InstanceOf() {
    }
}

/**
 * A Java type
 */
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

/**
 * A type casting operator
 */
class TypeCasting implements ExpressionElement {
    public final JavaType toType;

    public TypeCasting(JavaType toType) {
        this.toType = toType;
    }
}