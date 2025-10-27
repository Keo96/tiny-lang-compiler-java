package parser.ast;

import lexer.Token;

// Represents an assignment: x = y + 5;
public class AssignStmt extends Stmt {
    public final Token name;  // The 'x'
    public final Expr value; // The 'y + 5'

    public AssignStmt(Token name, Expr value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitAssignStmt(this);
    }
}