package javatoarm.token;

import javatoarm.token.operator.OperatorToken;

public interface Token {

    static Token getObject(String word) {
        Token token;

        if (word.length() == 1) {
            char c = word.charAt(0);
            if ((token = SplitterToken.get(c)) != null) {
                return token;
            }
            if ((token = BracketToken.get(c)) != null) {
                return token;
            }
            if ((token = MemberAccessToken.get(c)) != null) {
                return token;
            }
        }

        if ((token = ValueToken.get(word)) != null) {
            return token;
        }
        if ((token = OperatorToken.get(word)) != null) {
            return token;
        }
        if ((token = KeywordToken.get(word)) != null) {
            return token;
        }

        return new NameToken(word);
    }

    enum Type {
        SPLITTER, BRACKET, VALUE, OPERATOR, KEYWORD, STRING, MEMBER_ACCESS
    }

}
