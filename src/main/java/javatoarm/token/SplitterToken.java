package javatoarm.token;

public class SplitterToken implements Token {
    public final char splitter;

    public SplitterToken(char c) {
        switch (c) {
            case ';', ',' -> splitter = c;
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public Type getTokenType() {
        return Type.SPLITTER;
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

    public static SplitterToken get(char c) {
        try {
            return new SplitterToken(c);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
