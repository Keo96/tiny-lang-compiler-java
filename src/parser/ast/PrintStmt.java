package parser.ast;

import lexer.Token;

// Represents a print statement: print(x);
public class PrintStmt extends Stmt {
    public final Token name; // The 'x' to print

    public PrintStmt(Token name) {
        this.name = name;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitPrintStmt(this);
    }
}