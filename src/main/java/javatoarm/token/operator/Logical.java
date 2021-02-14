package javatoarm.token.operator;

public class Logical implements OperatorToken.Binary {
    public final boolean isAnd;

    private Logical(boolean isAnd) {
        this.isAnd = isAnd;
    }

    public static Logical get(String operator) {
        return switch (operator) {
            case "&&" -> new Logical(true);
            case "||" -> new Logical(false);
            default -> null;
        };
    }

    @Override
    public int getPrecedenceLevel() {
        return isAnd ? 4 : 3;
    }

}
