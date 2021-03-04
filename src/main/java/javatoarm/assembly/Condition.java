package javatoarm.assembly;

/**
 * Condition in assembly codes
 */
public enum Condition {
    EQUAL, UNEQUAL, GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, ALWAYS, NEVER;

    /**
     * Get a condition from the provided symbol
     *
     * @param symbol the symbol
     * @return the corresponding condition
     * @throws IllegalArgumentException if the symbol does not correspond to a valid condition
     */
    public static Condition getFromSymbol(String symbol) throws IllegalArgumentException {
        return switch (symbol) {
            case "==" -> EQUAL;
            case "!=" -> UNEQUAL;
            case ">" -> GREATER;
            case "<" -> LESS;
            case ">=" -> GREATER_EQUAL;
            case "<=" -> LESS_EQUAL;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Get the associated symbol
     *
     * @return the symbol representing the condition
     * @throws IllegalArgumentException if the condition does not have one
     */
    public String toSymbol() throws IllegalArgumentException {
        return switch (this) {
            case EQUAL -> "==";
            case UNEQUAL -> "!=";
            case GREATER -> ">";
            case LESS -> "<";
            case GREATER_EQUAL -> ">=";
            case LESS_EQUAL -> "<=";
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Get the opposite condition
     *
     * @return the opposite condition
     */
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