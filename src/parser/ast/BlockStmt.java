package parser.ast;

import java.util.List;

// Represents a block of code: { ... }
public class BlockStmt extends Stmt {
    public final List<Stmt> statements;

    public BlockStmt(List<Stmt> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitBlockStmt(this);
    }
}