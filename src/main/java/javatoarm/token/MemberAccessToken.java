package javatoarm.token;

public class MemberAccessToken implements Token {
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
}
