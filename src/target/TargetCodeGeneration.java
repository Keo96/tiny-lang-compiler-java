package target;

// Use the correct package name for your Quadruple
import icg.Quadruple;

import java.util.ArrayList;
import java.util.List;

/**
 * Step 6: Target Code Generation
 * Translates TAC (quadruples) into a simple stack-based assembly.
 *
 * Stack ISA:
 * LOAD <var>        ; push variable value
 * PUSH <const>      ; push integer literal
 * STORE <var>       ; pop -> var
 * ADD | SUB
 * CMP_LT            ; push 1/0
 * JMP <label>
 * JZ <label>        ; jump if top == 0 (pop)
 * JNZ <label>       ; jump if top != 0 (pop)
 * LABEL <label>
 * PRINT             ; pop and print
 * READ <var>        ; read into var
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

                // ---- assignment ----
                case "=": { // Changed from ASSIGN
                    emitLoad(asm, a1);
                    asm.add("STORE " + r);
                    break;
                }

                // ---- arithmetic ----
                case "+": { // Changed from ADD
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("ADD");
                    asm.add("STORE " + r);
                    break;
                }
                case "-": { // Changed from SUB
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("SUB");
                    asm.add("STORE " + r);
                    break;
                }
                // (Removed MUL/DIV as TinyLang doesn't have them)

                // ---- relational ----
                case "<": { // Changed from LT
                    emitLoad(asm, a1);
                    emitLoad(asm, a2);
                    asm.add("CMP_LT"); // Pushes 1 (true) or 0 (false)
                    asm.add("STORE " + r);
                    break;
                }
                // (Removed other comparison ops)

                // ---- branching ----
                case "IFZ": { // Changed from IF_FALSE
                    // Quad is: (IFZ, t1, L0, null)
                    emitLoad(asm, a1);
                    asm.add("JZ " + a2); // Changed from r to a2
                    break;
                }

                // ---- gotos / labels ----
                case "GOTO": {
                    // Quad is: (GOTO, L1, null, null)
                    asm.add("JMP " + a1); // Changed from r to a1
                    break;
                }
                case "LABEL": {
                    // Quad is: (LABEL, L0, null, null)
                    asm.add("LABEL " + a1); // Changed from r to a1
                    break;
                }

                // ---- I/O ----
                case "PRINT": {
                    // Quad is: (PRINT, x, null, null)
                    String v = (r != null) ? r : a1; // Your logic here is good
                    emitLoad(asm, v);
                    asm.add("PRINT");
                    break;
                }
                case "READ": {
                    // Quad is: (READ, y, null, null)
                    String v = (r != null) ? r : a1;
                    asm.add("READ " + v);
                    break;
                }

                // ---- fallback ----
                default: {
                    // Ignore DECL or NOP
                    if (op.equals("DECL") || op.equals("NOP")) {
                        break;
                    }
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
        if (operand == null || operand.equals("---") || operand.equals("null")) return;
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