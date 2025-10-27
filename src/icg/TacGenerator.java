package icg;

import lexer.Token;
import parser.ast.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Walks the semantically-checked AST and generates Three-Address Code (TAC)
 * in the form of Quadruples.
 */
public class TacGenerator implements StmtVisitor<Void>, ExprVisitor<String> {

    private final List<Quadruple> quads = new ArrayList<>();
    private int tempCounter = 0;
    private int labelCounter = 0;

    /**
     * Generates TAC for a list of statements (the whole program).
     */
    public List<Quadruple> generate(List<Stmt> statements) {
        quads.clear();
        tempCounter = 0;
        labelCounter = 0;

        for (Stmt statement : statements) {
            statement.accept(this);
        }

        return quads;
    }


    /**
     * Creates a new, unique temporary variable name (e.g., "t0", "t1").
     */
    private String newTemp() {
        return "t" + tempCounter++;
    }

    /**
     * Creates a new, unique label name (e.g., "L0", "L1").
     */
    private String newLabel() {
        return "L" + labelCounter++;
    }

    /**
     * Helper to add a new Quadruple to our list.
     */
    private void emit(String op, String arg1, String arg2, String result) {
        quads.add(new Quadruple(op, arg1, arg2, result));
    }


    @Override
    public Void visitBlockStmt(BlockStmt stmt) {
        for (Stmt statement : stmt.statements) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visitVarDeclStmt(VarDeclStmt stmt) {
        // In TAC, a declaration doesn't generate any code.
        // It's just a note for the symbol table.
        // We can optionally emit a "comment" quad if we want.
        // emit("declare", stmt.name.lexeme, "int", null);
        return null;
    }

    @Override
    public Void visitAssignStmt(AssignStmt stmt) {
        // 1. Visit the right-hand side expression first.
        // This will generate code for the expression and return
        // the "address" (name) of where the final result is.
        String valueAddr = stmt.value.accept(this);

        // 2. Emit the final assignment.
        emit("=", valueAddr, null, stmt.name.lexeme);
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt stmt) {
        String elseLabel = newLabel(); // e.g., L0
        String endLabel = newLabel();  // e.g., L1

        // 1. Visit the condition
        String condAddr = stmt.condition.accept(this);

        // 2. Emit the conditional jump
        // "If condition is zero (false), jump to elseLabel"
        emit("IFZ", condAddr, elseLabel, null);

        // 3. Visit the "then" branch
        stmt.thenBranch.accept(this);

        // 4. Emit the unconditional jump to skip the 'else' block
        emit("GOTO", endLabel, null, null);

        // 5. Emit the 'else' label
        emit("LABEL", elseLabel, null, null);

        // 6. Visit the "else" branch
        if (stmt.elseBranch != null) {
            stmt.elseBranch.accept(this);
        }

        // 7. Emit the final 'end' label
        emit("LABEL", endLabel, null, null);

        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt stmt) {
        String loopTop = newLabel(); // e.g., L0
        String loopEnd = newLabel(); // e.g., L1

        // 1. Emit the label for the top of the loop
        emit("LABEL", loopTop, null, null);

        // 2. Visit the condition
        String condAddr = stmt.condition.accept(this);

        // 3. Emit the conditional jump to exit the loop
        // "If condition is zero (false), jump to loopEnd"
        emit("IFZ", condAddr, loopEnd, null);

        // 4. Visit the loop body
        stmt.body.accept(this);

        // 5. Emit the unconditional jump back to the top
        emit("GOTO", loopTop, null, null);

        // 6. Emit the 'end' label
        emit("LABEL", loopEnd, null, null);

        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStmt stmt) {
        // We'll create a "PRINT" op for our TAC.
        emit("PRINT", stmt.name.lexeme, null, null);
        return null;
    }

    @Override
    public Void visitReadStmt(ReadStmt stmt) {
        // We'll create a "READ" op for our TAC.
        emit("READ", stmt.name.lexeme, null, null);
        return null;
    }

    @Override
    public String visitBinaryExpr(BinaryExpr expr) {
        // 1. Recursively visit left and right sides
        String leftAddr = expr.left.accept(this);
        String rightAddr = expr.right.accept(this);

        // 2. Create a new temporary to hold the result
        String resultAddr = newTemp(); // e.g., "t0"

        // 3. Emit the instruction
        emit(expr.operator.lexeme, leftAddr, rightAddr, resultAddr);

        // 4. Return the *name* of the temporary
        return resultAddr;
    }

    @Override
    public String visitLiteralExpr(LiteralExpr expr) {
        // The "address" of a literal is just the literal itself.
        return expr.value.toString();
    }

    @Override
    public String visitVariableExpr(VariableExpr expr) {
        // The "address" of a variable is just its name.
        return expr.name.lexeme;
    }

    @Override
    public String visitGroupingExpr(GroupingExpr expr) {
        // The "address" of a (group) is just the address
        // of the expression inside it. No code is generated
        // for the parentheses themselves.
        return expr.expression.accept(this);
    }
}