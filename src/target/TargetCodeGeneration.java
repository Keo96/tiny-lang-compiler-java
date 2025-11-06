package target;

import icg.Quadruple;

import java.util.ArrayList;
import java.util.List;

/**
 * Step 6: Target Code Generation
 * Translates TAC (quadruples) into a simple stack-based assembly.
 *
 * Stack ISA:
 *   LOAD <var>         ; push variable value
 *   PUSH <const>       ; push integer literal
 *   STORE <var>        ; pop -> var
 *   ADD | SUB | MUL | DIV
 *   CMP_LT | CMP_LE | CMP_GT | CMP_GE | CMP_EQ | CMP_NE   ; push 1/0
 *   JMP <label>
 *   JZ <label>         ; jump if top == 0, (non-destructive check assumed)
 *   JNZ <label>        ; jump if top != 0
 *   LABEL <label>
 *   PRINT              ; pop and print
 *   READ <var>         ; read into var
 *
 * Notes:
 * - DECL (variable declarations) are ignored here (backend has no alloc model).
 * - ASSIGN a -> r   becomes:  LOAD/PUSH a ; STORE r
 * - For PRINT, we accept either PRINT x (x in result or arg1).
 * - For IF / IF_FALSE, we expect condition already in a temp or use boolean result from prior CMP_*.
 */
public class TargetCodeGeneration {

    public List<String> generate(List<Quadruple> quads) {
        List<String> asm = new ArrayList<>();

        for (Quadruple q : quads) {
            String op = safe(q.op());
            String a1 = q.arg1();
            String a2 = q.arg2();
            String r  = q.result();

            switch (op) {

                // ---- housekeeping / declarations ----
                case "DECL":         // e.g., DECL null null x
                case "NOP":
                    // No emission for declarations at this backend level.
                    break;

                // ---- assignment ----
                case "ASSIGN": {     // r = a1
                    emitLoad(asm, a1);
                    asm.add("STORE " + r);
                    break;
                }

                // ---- arithmetic ----
                case "ADD": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("ADD");
                    asm.add("STORE " + r);
                    break;
                }
                case "SUB": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("SUB");
                    asm.add("STORE " + r);
                    break;
                }
                case "MUL": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("MUL");
                    asm.add("STORE " + r);
                    break;
                }
                case "DIV": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("DIV");
                    asm.add("STORE " + r);
                    break;
                }

                // ---- relational into boolean temp ----
                case "LT": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("CMP_LT");
                    asm.add("STORE " + r);
                    break;
                }
                case "LE": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("CMP_LE");
                    asm.add("STORE " + r);
                    break;
                }
                case "GT": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("CMP_GT");
                    asm.add("STORE " + r);
                    break;
                }
                case "GE": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("CMP_GE");
                    asm.add("STORE " + r);
                    break;
                }
                case "EQ": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("CMP_EQ");
                    asm.add("STORE " + r);
                    break;
                }
                case "NE": {
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("CMP_NE");
                    asm.add("STORE " + r);
                    break;
                }

                // ---- branching ----
                // IF t -> label : jump if true (non-zero)
                case "IF": {         // IF a1 GOTO r
                    emitLoad(asm, a1);
                    asm.add("JNZ " + r);
                    break;
                }
                // IF_FALSE t -> label : jump if false (zero)
                case "IF_FALSE": {   // IF_FALSE a1 GOTO r
                    emitLoad(asm, a1);
                    asm.add("JZ " + r);
                    break;
                }

                // ---- gotos / labels ----
                case "GOTO": {
                    asm.add("JMP " + r);
                    break;
                }
                case "LABEL": {
                    asm.add("LABEL " + r);
                    break;
                }

                // ---- I/O ----
                case "PRINT": {
                    // Accept either result or arg1 as the print operand
                    String v = (r != null) ? r : a1;
                    emitLoad(asm, v);
                    asm.add("PRINT");
                    break;
                }
                case "READ": {
                    // READ into result or arg1 (favor result)
                    String v = (r != null) ? r : a1;
                    asm.add("READ " + v);
                    break;
                }

                // ---- fallback ----
                default: {
                    asm.add("; TODO: unrecognized op " + op
                            + " a1=" + String.valueOf(a1)
                            + " a2=" + String.valueOf(a2)
                            + " r=" + String.valueOf(r));
                }
            }
        }
        return asm;
    }

    // helpers

    private void emitLoad(List<String> asm, String operand) {
        if (operand == null || operand.equals("---")) return;
        if (isIntegerLiteral(operand)) {
            asm.add("PUSH " + operand);
        } else {
            asm.add("LOAD " + operand);
        }
    }

    private boolean isIntegerLiteral(String s) {
        return s != null && s.matches("-?\\d+");
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}