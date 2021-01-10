package javatoarm.lexer;

public class JavaLexerToken {
    private final Type tokenType;

    protected JavaLexerToken(Type type) {
        this.tokenType = type;
    }

    public static JavaLexerToken getObject(String word) {
        JavaLexerToken token;

        if (word.length() == 1) {
            char c = word.charAt(0);
            if ((token = JavaLexerSplitter.get(c)) != null) {
                return token;
            }
            if ((token = JavaLexerBracket.get(c)) != null) {
                return token;
            }
        }

        if ((token = JavaLexerValue.get(word)) != null) {
            return token;
        }
        if ((token = JavaLexerOperator.get(word)) != null) {
            return token;
        }
        if ((token = JavaLexerKeyword.get(word)) != null) {
            return token;
        }

        return JavaLexerString.obtain(word);
    }

    enum Type {
        SPLITTER, BRACKET, VALUE, OPERATOR, KEYWORD, STRING, CONTEXT_DEPENDENT
    }

}
