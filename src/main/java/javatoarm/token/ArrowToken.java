package javatoarm.token;

public enum ArrowToken implements Token {
    INSTANCE;

    public static ArrowToken get(String s) {
        return s.equals("->") ? INSTANCE : null;
    }


    @Override
    public String toString() {
        return "->";
    }
}
