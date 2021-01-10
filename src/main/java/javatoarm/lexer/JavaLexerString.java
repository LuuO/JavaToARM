package javatoarm.lexer;

public class JavaLexerString extends JavaLexerToken {
    private final String string;

    private JavaLexerString(String s) {
        super(Type.STRING);
        string = s;
    }

    public static JavaLexerToken obtain(String s) {
        return new JavaLexerString(s);
    }

    public boolean isValidName() {
        if (string.length() == 0 || Character.isDigit(string.charAt(0))) {
            return false;
        }
        for (char c : string.toCharArray()) {
            if (!JavaLexer.isNameableChar(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isCharacter() {
        return string.length() == 1;
    }

    public char toCharacter() {
        if (!isCharacter()) {
            throw new UnsupportedOperationException();
        }

        return string.charAt(0);
    }
}
