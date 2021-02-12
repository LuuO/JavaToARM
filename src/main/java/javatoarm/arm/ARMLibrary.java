package javatoarm.arm;

import java.util.List;

public class ARMLibrary {

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

    public static String mallocInit() {
        return """
            		LDR		R0, =heap_start
            		LDR		R1, =heap_front
            		STR		R0, [R1]
            """;
    }

    public static String heapStartLabel() {
        return """
            heap_start:		.word 0xEEEEEEEE
            """;
    }
}
