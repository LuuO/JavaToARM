package javatoarm.token;

import javatoarm.JTAException;

public class NameToken implements Token, CharSequence {
    private final String string;

    public NameToken(String s) throws JTAException {
        if (!isValidName(s)) {
            throw new JTAException.InvalidName(s + " is an invalid name.");
        }
        string = s;
    }

    public static boolean isValidName(String name) {
        if (name.length() == 0 || Character.isDigit(name.charAt(0))) {
            return false;
        }
        for (char c : name.toCharArray()) {
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
