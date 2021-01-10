package javatoarm.lexer;

public class JavaLexerKeyword extends JavaLexerToken {
//    Set<String> keywords = Set.of("abstract", "continue", "for", "new", "switch",
//        "assert", "default", "goto", "package", "synchronized",
//        "boolean", "do", "if", "private", "this",
//        "break", "double", "implements", "protected", "throw",
//        "byte", "else", "import", "public", "throws",
//        "case""enum""instanceof""return""transient"
//        "catch""extends""int""short""try"
//        "char""final""interface""static""void"
//        "class""finally""long""strictfp" * * "volatile"
//"const"*"float""native""super""while")
    // Src https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
    // "The keywords const and goto are reserved, even though they are not currently used.
    // true, false, and null might seem like keywords, but they are actually literals"

    private final Keyword keyword;

    private JavaLexerKeyword(Keyword keyword) {
        super(Type.KEYWORD);
        this.keyword = keyword;
    }

    public static JavaLexerKeyword get(String keyword) {
        try {
            return new JavaLexerKeyword(Keyword.valueOf("_" + keyword));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public enum Keyword {
        _abstract, _continue, _for, _new, _switch,
        _assert, _default, _goto, _package, _synchronized,
        _boolean, _do, _if, _private, _this,
        _break, _double, _implements, _protected, _throw,
        _byte, _else, _import, _public, _throws, _case, _enum, _instanceof, _return, _transient,
        _catch, _extends, _int, _short, _try, _char, _final, _interface, _static, _void, _class,
        _finally, _long, _strictfp, _volatile,
        _const, _float, _native, _super, _while
    }

}
