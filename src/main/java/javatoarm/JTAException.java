package javatoarm;

import javatoarm.java.JavaExpression;
import javatoarm.java.JavaProperty;
import javatoarm.java.JavaType;
import javatoarm.token.Token;

public abstract class JTAException extends Exception {
    private JTAException(String message) {
        super(message);
    }

    public static class UnknownCharacter extends JTAException {
        public UnknownCharacter(char c) {
            super("Unknown character: '" + c + "'");
        }
    }

    public static class UnexpectedToken extends JTAException {
        public UnexpectedToken(String expected, String actual) {
            super("Unexpected Token: " + actual + ", expected: " + expected);
        }

        public UnexpectedToken(Token expected, Token actual) {
            this(expected.toString(), actual.toString());
        }

        public UnexpectedToken(String expected, Token actual) {
            this(expected, actual.toString());
        }
    }

    public static class UnsupportedProperty extends JTAException {
        public UnsupportedProperty(JavaProperty property) {
            super("Unsupported property: " + property);
        }
    }

    public static class OutOfRegister extends JTAException {
        public OutOfRegister() {
            super("Our of registers :(");
        }
    }

    public static class VariableAlreadyDeclared extends JTAException {
        public VariableAlreadyDeclared(String name) {
            super("Variable \"" + name + "\" is already declared.");
        }
    }

    public static class TypeMismatch extends JTAException {
        public TypeMismatch(JavaType expected, JavaType actual) {
            super("Unexpected Type: " + actual + ", expected: " + expected);
        }
    }

    public static class DanglingToken extends JTAException {
        public DanglingToken(Token token) {
            super("Dangling token: " + token);
        }
    }

    public static class InvalidOperation extends JTAException {
        public InvalidOperation(String message) {
            super(message);
        }
    }

    public static class NotAStatement extends JTAException {
        public NotAStatement() {
            super("Not a statement");
        }
    }

    public static class InvalidExpression extends JTAException {
        public InvalidExpression(JavaExpression expression) {
            super("Not a statement");
        }
    }
}


