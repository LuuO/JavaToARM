package javatoarm.lexer;

public class JavaLexerSplitter extends JavaLexerToken {
    public final char splitter;

    JavaLexerSplitter(char c) {
        super(Type.SPLITTER);
        switch (c) {
            case ';', ',' -> splitter = c;
            default -> throw new IllegalArgumentException();
        }
    }

    public static JavaLexerSplitter get(char c) {
        try {
            return new JavaLexerSplitter(c);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
