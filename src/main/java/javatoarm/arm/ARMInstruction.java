package javatoarm.arm;

public class ARMInstruction {

    public static void instruction(StringBuilder text, OP op, int Rn, int Rd) {

    }

    public static void returnInstruction(StringBuilder text) {
        text.append("\tMOV\tPC, LR");
    }

    public static void pushCallerSave(StringBuilder text) {
        text.append("\tPUSH\t{R1-R3, R11, LR}"); //TODO R11?
    }

    public static void pushCalleeSave(StringBuilder text) {
        text.append("\tPUSH\t{R4-R12}"); //TODO R11?
    }

    public static void popCallerSave(StringBuilder text) {
        text.append("\tPOP\t{R1-R3, R11, LR}"); //TODO R11?
    }

    public static void popCalleeSave(StringBuilder text) {
        text.append("\tPOP\t{R4-R12}"); //TODO R11?
    }

    public static void branch(StringBuilder text, OP op, String label) {
        if (op != OP.B && op != OP.BL) {
            throw new IllegalArgumentException();
        }
        text.append('\t').append(op.name()).append('\t').append(label).append('\n');
    }

    public static void label(StringBuilder text, String label) {
        text.append(label).append(":\n");
    }

    public enum OP {
        B, BL, MOV, MVN, ADR, LDR, ADD, ADC, SUB, LSL, LSR
    }
}
