package parser.ast;

// This is the "parent" abstract class for all expression nodes.
public abstract class Expr {
    public abstract <R> R accept(ExprVisitor<R> visitor);
}