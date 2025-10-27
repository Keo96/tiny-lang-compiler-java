package parser.ast;

public abstract class Stmt {
    public abstract <R> R accept(StmtVisitor<R> visitor);
}