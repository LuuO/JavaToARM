package javatoarm.token.operator;

import javatoarm.assembly.Condition;

public class Comparison implements OperatorToken.Binary {
    public final Condition condition;

    private Comparison(Condition condition) {
        this.condition = condition;
    }

    public static Comparison get(String operator) {
        try {
            return new Comparison(Condition.getFromSymbol(operator));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public int getPrecedenceLevel() {
        return switch (condition) {
            case EQUAL, UNEQUAL -> 8;
            default -> 9;
        };
    }

    @Override
    public OperatorToken.Binary.Type getBinaryOperatorType() {
        return OperatorToken.Binary.Type.COMPARISON;
    }
}
