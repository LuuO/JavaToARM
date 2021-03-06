package javatoarm.token;

/**
 * Represents a keyword in Java
 */
public enum KeywordToken implements Token {

    /**
     * Source:
     * https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
     * <p>
     * "The keywords const and goto are reserved, even though they are not currently used.
     * true, false, and null might seem like keywords, but they are actually literals"
     * </p>
     */

    _abstract, _continue, _for, _new, _switch, _assert, _default, _goto, _package,
    _synchronized, _boolean, _do, _if, _private, _this, _break, _double, _implements,
    _protected, _throw, _byte, _else, _import, _public, _throws, _case, _enum, _instanceof,
    _return, _transient, _catch, _extends, _int, _short, _try, _char, _final, _interface,
    _static, _void, _class, _finally, _long, _strictfp, _volatile, _const, _float, _native,
    _super, _while;

    /**
     * Get a keyword token
     *
     * @param keyword the keyword string
     * @return if the keyword string is a valid keyword, returns the corresponding token.
     * Otherwise returns null.
     */
    public static KeywordToken get(String keyword) {
        try {
            return valueOf("_" + keyword);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return name().substring(1);
    }

}
