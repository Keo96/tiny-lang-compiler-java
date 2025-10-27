package parser.ast;

import lexer.Token;

// Represents a read statement: read(y);
public class ReadStmt extends Stmt {
    public final Token name; // The 'y' to read into

    public ReadStmt(Token name) {
        this.name = name;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitReadStmt(this);
    }
}