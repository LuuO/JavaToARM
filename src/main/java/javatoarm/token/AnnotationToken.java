package javatoarm.token;

public class AnnotationToken implements Token {
    private final static int HASH_CODE = 999;

    public static AnnotationToken INSTANCE = new AnnotationToken();

    private AnnotationToken() {
    }

    public static AnnotationToken get(char c) {
        if (c == '@') {
            return INSTANCE;
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return HASH_CODE;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public String toString() {
        return "@";
    }
}
