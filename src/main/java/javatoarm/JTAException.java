package javatoarm;

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
    }
}


