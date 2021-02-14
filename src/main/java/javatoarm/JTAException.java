package javatoarm;

import javatoarm.java.JavaProperty;
import javatoarm.java.type.JavaType;
import javatoarm.java.expression.JavaExpression;
import javatoarm.token.Token;

/**
 * Java to ARM checked exceptions
 */
public abstract class JTAException extends Exception {
    private JTAException(String message) {
        super(message);
    }

    public static class UnknownCharacter extends JTAException {
        public UnknownCharacter(char c) {
            super("Unknown character: '" + c + "'");
        }

        public UnknownCharacter(String s) {
            super("Unknown character: '" + s + "'");
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
            super("Unsupported: " + property);
        }
    }

    public static class OutOfRegister extends JTAException {
        public OutOfRegister() {
            super("Our of registers :(");
        }
    }

    public static class VariableAlreadyDeclared extends JTAException {
        public VariableAlreadyDeclared(String name) {
            super("LocalVariable \"" + name + "\" is already declared.");
        }
    }

    public static class TypeMismatch extends JTAException {
        public TypeMismatch(JavaType expected, JavaType actual) {
            super("Unexpected Condition: " + actual + ", expected: " + expected);
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
            super("Invalid Expression");
        }
    }

    public static class Unsupported extends JTAException {
        public Unsupported(String message) {
            super(message);
        }
    }

    public static class InvalidName extends JTAException {
        public InvalidName(String message) {
            super(message);
        }
    }

    public static class NotInALoop extends JTAException {
        public NotInALoop(String message) {
            super(message);
        }
    }

    public static class FunctionAlreadyDeclared extends JTAException {
        public FunctionAlreadyDeclared(String message) {
            super(message);
        }
    }

    public static class UnknownFunction extends JTAException {
        public UnknownFunction(String message) {
            super(message);
        }
    }

}


