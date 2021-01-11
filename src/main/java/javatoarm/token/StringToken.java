package javatoarm.token;

public class StringToken implements Token, CharSequence {
    private final String string;

    public StringToken(String s) {
        string = s;
    }

    @Override
    public Type getTokenType() {
        return Type.STRING;
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

    @Override
    public String toString() {
        return string;
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

    // CharSequence

    @Override
    public int length() {
        return string.length();
    }

    @Override
    public char charAt(int index) {
        return string.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return string.substring(start, end);
    }
}
