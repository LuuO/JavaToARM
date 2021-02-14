package javatoarm.token;

public class KeywordToken implements Token {
    public static KeywordToken NEW = new KeywordToken(Keyword._new);
    public static KeywordToken THIS = new KeywordToken(Keyword._this);

    public final Keyword keyword;

    public KeywordToken(Keyword keyword) {
        this.keyword = keyword;
    }

    public static KeywordToken get(String keyword) {
        try {
            return new KeywordToken(Keyword.valueOf("_" + keyword));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeywordToken) {
            KeywordToken that = (KeywordToken) obj;
            return this.keyword.equals(that.keyword);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return keyword.hashCode();
    }

    @Override
    public String toString() {
        return keyword.name().substring(1);
    }

    public enum Keyword {
        /**
         * Src https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
         * "The keywords const and goto are reserved, even though they are not currently used.
         * true, false, and null might seem like keywords, but they are actually literals"
         */

        _abstract, _continue, _for, _new, _switch, _assert, _default, _goto, _package,
        _synchronized, _boolean, _do, _if, _private, _this, _break, _double, _implements,
        _protected, _throw, _byte, _else, _import, _public, _throws, _case, _enum, _instanceof,
        _return, _transient, _catch, _extends, _int, _short, _try, _char, _final, _interface,
        _static, _void, _class, _finally, _long, _strictfp, _volatile, _const, _float, _native,
        _super, _while
    }

}
