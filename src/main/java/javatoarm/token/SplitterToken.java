package javatoarm.token;

public class SplitterToken implements Token {
    public static final SplitterToken COMMA = new SplitterToken(',');
    public static final SplitterToken SEMI_COLON = new SplitterToken(';');

    public final char splitter;

    private SplitterToken(char c) {
        switch (c) {
            case ';', ',' -> splitter = c;
            default -> throw new IllegalArgumentException();
        }
    }

    public static SplitterToken get(char c) {
        try {
            return new SplitterToken(c);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return splitter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SplitterToken) {
            return ((SplitterToken) obj).splitter == this.splitter;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(splitter);
    }

}
