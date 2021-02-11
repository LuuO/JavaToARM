package javatoarm.arm;

import javatoarm.assembly.Condition;
import javatoarm.assembly.Register;

public class ARMInstruction {

    public static void instruction(StringBuilder text, OP op, Register Rd, Register Rn) {
        text.append("\t\t%s\t\t%s, %s\n".formatted(op.name(), Rd, Rn));
    }

    public static void instruction(StringBuilder text, OP op, Register Rd, Register Rn, int imm) {
        text.append("\t\t%s\t\t%s, %s, %d\n".formatted(op.name(), Rd, Rn, imm));
    }

    public static void move(StringBuilder text, Condition condition, Register Rd, Register Rn) {
        text.append("\t\tMOV%s\t\t%s, %s\n".formatted(toCode(condition), Rd, Rn));
    }

    public static void move(StringBuilder text, Condition condition, Register Rd, int imm) {
        text.append("\t\tMOV%s\t\t%s, %d\n".formatted(toCode(condition), Rd, imm));
    }

    public static void store(StringBuilder text, Condition condition,
                             Register src, Register base, Register index, int leftShift) {
        text.append("\t\tSTR%s\t\t%s, [%s, %s, LSL#%d]\n"
            .formatted(toCode(condition), src, base, index, leftShift));
    }

    public static void returnInstruction(StringBuilder text) {
        text.append("\t\tMOV\t\tPC, LR\n");
    }

    public static void pushCallerSave(StringBuilder text) {
        text.append("\t\tPUSH\t{R1-R3, LR}\n"); //TODO R11?
    }

    public static void pushCalleeSave(StringBuilder text) {
        text.append("\t\tPUSH\t\t{R4-R11}\n"); //TODO R11?
    }

    public static void popCallerSave(StringBuilder text) {
        text.append("\t\tPOP\t\t{R1-R3, LR}\n"); //TODO R11?
    }

    public static void popCalleeSave(StringBuilder text) {
        text.append("\t\tPOP\t\t{R4-R11}\n"); //TODO R11?
    }

    public static void branch(StringBuilder text, Condition condition, OP op, String label) {
        if (op != OP.B && op != OP.BL) {
            throw new IllegalArgumentException();
        }
        text.append("\t\t%s%s\t\t%s\n".formatted(op.name(), toCode(condition), label));
    }

    public static void label(StringBuilder text, String label) {
        text.append(label).append(":\n");
    }

    private static String toCode(Condition condition) {
        return switch (condition) {
            case EQUAL -> "EQ";
            case UNEQUAL -> "NE";
            case GREATER -> "GT";
            case LESS -> "LT";
            case GREATER_EQUAL -> "GE";
            case LESS_EQUAL -> "LE";
            case ALWAYS -> "";
            case NEVER -> throw new IllegalArgumentException();
        };
    }

}
