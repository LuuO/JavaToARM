package javatoarm.arm;

import javatoarm.assembly.Condition;
import javatoarm.assembly.Register;

import java.util.List;

public class ARMInstruction {

    public static void instruction(StringBuilder text, OP op, Register Rd, Register Rn) {
        text.append("\t\t%s\t\t%s, %s\n".formatted(op.name(), Rd, Rn));
    }

    public static void instruction(StringBuilder text, OP op, Register Rd, int imm) {
        text.append("\t\t%s\t\t%s, #%d\n".formatted(op.name(), Rd, imm));
    }

    public static void instruction(StringBuilder text, OP op, Register Rd, Register Rn,
                                   Register Rm) {
        text.append("\t\t%s\t\t%s, %s, %s\n".formatted(op.name(), Rd, Rn, Rm));
    }

    public static void instruction(StringBuilder text, OP op, boolean cc, Register Rd, Register Rn,
                                   Register Rm) {
        text.append("\t\t%s%s\t\t%s, %s, %s\n".formatted(op.name(), cc ? "S" : "", Rd, Rn, Rm));
    }

    public static void instruction(StringBuilder text, OP op, Register Rd, Register Rn, int imm) {
        text.append("\t\t%s\t\t%s, %s, #%d\n".formatted(op.name(), Rd, Rn, imm));
    }

    public static void move(StringBuilder text, Condition condition, Register Rd, Register Rn) {
        if (Rd.equals(Rn)) {
            return;
        }
        text.append("\t\tMOV%s\t\t%s, %s\n".formatted(toCode(condition), Rd, Rn));
    }

    public static void move(StringBuilder text, Condition condition, Register Rd, int imm) {
        text.append("\t\tMOV%s\t\t%s, #%d\n".formatted(toCode(condition), Rd, imm));
    }

    public static void store(StringBuilder text, Condition condition,
                             Register src, Register base, Register index, int leftShift) {
        text.append("\t\tSTR%s\t\t%s, [%s, %s, LSL#%d]\n"
                .formatted(toCode(condition), src, base, index, leftShift));
    }

    public static void returnInstruction(StringBuilder text) {
        text.append("\t\tMOV\t\tPC, LR\n");
    }

    public static void push(StringBuilder text, List<Register> registers) {
        if (text.isEmpty()) {
            return;
        }
        text.append("\t\tPUSH\t{").append(registers.get(0));
        for (int i = 1; i < registers.size(); i++) {
            text.append(", ").append(registers.get(i));
        }
        text.append("}\n");
    }

    public static void pushCalleeSave(StringBuilder text) {
        text.append("\t\tPUSH\t{R4-R11}\n");
    }

    public static void pop(StringBuilder text, List<Register> registers) {
        if (text.isEmpty()) {
            return;
        }
        text.append("\t\tPOP\t\t{").append(registers.get(0));
        for (int i = 1; i < registers.size(); i++) {
            text.append(", ").append(registers.get(i));
        }
        text.append("}\n");
    }

    public static void popCalleeSave(StringBuilder text) {
        text.append("\t\tPOP\t\t{R4-R11}\n");
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

    public static void load(StringBuilder text, Register Rd, Register base, Register index,
                            int leftShift) {
        text.append("\t\tLDR\t\t%s, [%s, %s, LSL#%d]\n".formatted(Rd, base, index, leftShift));
    }

    public static void load(StringBuilder text, Register Rd, String label) {
        text.append("\t\tLDR\t\t%s, =%s\n".formatted(Rd, label));
    }

    public static void load(StringBuilder text, Register des, Register src) {
        text.append("\t\tLDR\t\t%s, [%s]\n".formatted(des, src));
    }

    public static void labelValuePair(StringBuilder text, String label, Integer value) {
        text.append("%s:\t.word %s\n".formatted(label, value));
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
