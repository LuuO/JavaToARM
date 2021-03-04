package javatoarm.arm;

import javatoarm.assembly.InstructionSet;
import javatoarm.assembly.Register;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Some useful ARM instructions
 */
public class ARMLibrary {
    public final static Register[] Registers = IntStream.range(0, 16).boxed()
            .map(i -> new Register(i, InstructionSet.ARMv7)).toArray(Register[]::new);

    /**
     * Get the beginning part of the program.
     *
     * @param entryClass      name of the entry class
     * @param mainOffset      offset to the main function
     * @param stackPosition   initial stack position
     * @param initializations other instructions
     * @return the beginning part of the program
     */
    public static String start(String entryClass, int mainOffset, int stackPosition,
                               List<String> initializations) {
        return """
                            
                .global	_start
                _start:
                		LDR		R0, =stack_start
                		LDR		SP, [R0]
                %s		LDR		R0, =%s
                		ADD		R0, R0, #%d
                		BLX		R0
                IDLE:
                		B		IDLE
                .data
                stack_start:	.word 0x%x
                .text
                            
                """.formatted(String.join("", initializations), entryClass, mainOffset, stackPosition);
    }

    /**
     * A mini malloc subroutine that allocates a piece of heap memory
     *
     * @return the malloc subroutine
     */
    public static String malloc() {
        return """
                            
                .global	_malloc
                _malloc:
                		LDR		R1, =heap_front
                		LDR		R2, [R1]
                		ADD		R3, R2, R0
                		STR		R3, [R1]
                		MOV		R0, R2
                		MOV		PC, LR
                .data
                heap_front:		.word 0
                .text
                            
                """;
    }

    /**
     * Instructions to initialize the heap space.
     *
     * @return the required instructions
     */
    public static String heapInit() {
        return """
                		LDR		R0, =heap_start
                		LDR		R1, =heap_front
                		STR		R0, [R1]
                """;
    }

    /**
     * A label indicating the start of the heap space.
     * Should be placed at the very end of the program.
     *
     * @return the heap start label
     */
    public static String heapStartLabel() {
        return """
                            
                .data
                heap_start:		.word 0xEEEEEEEE
                """;
    }
}
