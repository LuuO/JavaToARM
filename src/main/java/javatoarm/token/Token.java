package javatoarm.token;

import javatoarm.JTAException;
import javatoarm.token.operator.OperatorToken;
import javatoarm.token.operator.TernaryToken;

public interface Token {

    /**
     * Given a word, returns the corresponding token.
     *
     * @param word the word
     * @return the corresponding token
     * @throws JTAException.InvalidName if the meaning of the word is unknown
     */
    static Token getObject(String word) throws JTAException.InvalidName {
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
            if ((token = AnnotationToken.get(c)) != null) {
                return token;
            }
            if ((token = TernaryToken.get(c)) != null) {
                return token;
            }
        }

        if ((token = ImmediateToken.get(word)) != null) {
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

}
