package javatoarm.token;

import javatoarm.JTAException;

/**
 * Represent a type, variable, function, or class name in Java
 */
public class NameToken implements Token {
    private final String string;

    /**
     * Create a name token with the provided string
     *
     * @param s name
     * @throws JTAException.InvalidName if the string is not a valid name
     */
    public NameToken(String s) throws JTAException.InvalidName {
        if (!isValidName(s)) {
            throw new JTAException.InvalidName(s + " is not a valid name.");
        }
        string = s;
    }

    /**
     * Check if the provided string is a valid name in Java
     *
     * @param name the string
     * @return true if the name is valid, false otherwise
     */
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

}
