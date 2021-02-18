package javatoarm.token.operator;

public enum Logical implements OperatorToken.Binary {
    AND, OR;

    public static Logical get(String operator) {
        return switch (operator) {
            case "&&" -> AND;
            case "||" -> OR;
            default -> null;
        };
    }

    @Override
    public int getPrecedenceLevel() {
        return switch (this) {
            case AND -> 4;
            case OR -> 3;
        };
    }

}
