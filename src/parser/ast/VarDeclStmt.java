package parser.ast;

import lexer.Token;

public class VarDeclStmt extends Stmt {
    public final Token name;

    public VarDeclStmt(Token name) {
        this.name = name;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitVarDeclStmt(this);
    }
}