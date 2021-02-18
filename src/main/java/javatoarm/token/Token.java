package javatoarm.token;

import javatoarm.JTAException;
import javatoarm.token.operator.OperatorToken;
import javatoarm.token.operator.QuestColon;

/**
 * Represents a token
 */
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
            if ((token = CharToken.get(c)) != null) {
                return token;
            }
            if ((token = BracketToken.get(c)) != null) {
                return token;
            }
            if ((token = QuestColon.get(c)) != null) {
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
        if ((token = ArrowToken.get(word)) != null) {
            return token;
        }

        return new NameToken(word);
    }

//    /**
//     * A helper function to compare keywords
//     *
//     * @param keyword the keyword to compare
//     * @return true if this is an instance of KeywordToken and the keyword of this equals to the provided keyword,
//     * false otherwise.
//     */
//    default boolean equals(KeywordToken.Keyword keyword) {
//        if (this instanceof KeywordToken) {
//            return this.equals(keyword);
//        }
//        return false;
//    }
}
