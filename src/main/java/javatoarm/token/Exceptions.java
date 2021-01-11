package javatoarm.token;

public abstract class Exceptions extends Exception {
    private Exceptions(String message) {
        super(message);
    }

    static class UnknownCharacter extends Exceptions {
        public UnknownCharacter(char c) {
            super("Unknown character: '" + c + "'");
        }
    }

    public static class UnexpectedToken extends Exceptions {
        public UnexpectedToken(String expected, String actual) {
            super("Unexpected Token: " + actual + ", expected: " + expected);
        }

        public UnexpectedToken(Token expected, Token actual) {
            this(expected.toString(), actual.toString());
        }
    }
}


