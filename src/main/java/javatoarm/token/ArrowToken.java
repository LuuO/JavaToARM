package javatoarm.token;

public class ArrowToken implements Token {
    public static final ArrowToken INSTANCE = new ArrowToken();

    private ArrowToken() {
    }

    public static ArrowToken get(String s) {
        return s.equals("->") ? INSTANCE : null;
    }
}
