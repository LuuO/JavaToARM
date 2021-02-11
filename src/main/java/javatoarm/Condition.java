package javatoarm;

public enum Condition {
    EQUAL, UNEQUAL, GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, ALWAYS, NEVER;

    public static Condition getFromSymbol(String name) throws IllegalArgumentException {
        return switch (name) {
            case "==" -> EQUAL;
            case "!=" -> UNEQUAL;
            case ">" -> GREATER;
            case "<" -> LESS;
            case ">=" -> GREATER_EQUAL;
            case "<=" -> LESS_EQUAL;
            default -> throw new IllegalArgumentException();
        };
    }

    public Condition opposite() {
        return switch (this) {
            case EQUAL -> UNEQUAL;
            case UNEQUAL -> EQUAL;
            case GREATER -> LESS_EQUAL;
            case LESS -> GREATER_EQUAL;
            case GREATER_EQUAL -> LESS;
            case LESS_EQUAL -> GREATER;
            case ALWAYS -> NEVER;
            case NEVER -> ALWAYS;
        };
    }
}