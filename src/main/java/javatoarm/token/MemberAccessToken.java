package javatoarm.token;

import javatoarm.token.operator.OperatorToken;

public class MemberAccessToken implements OperatorToken {
    public static MemberAccessToken INSTANCE = new MemberAccessToken();

    private MemberAccessToken() {
    }

    static MemberAccessToken get(char c) {
        if (c == '.') {
            return INSTANCE;
        } else {
            return null;
        }
    }

    @Override
    public int getPrecedenceLevel() {
        return 16;
    }
}
