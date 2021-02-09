package javatoarm.token;

public class MemberAccessToken implements Token {

    static MemberAccessToken get(char c) {
        if (c == '.')
            return new MemberAccessToken();
        else
            return null;
    }
}
