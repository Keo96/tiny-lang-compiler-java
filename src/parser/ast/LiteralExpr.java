package parser.ast;

// Represents a literal (constant) value: 5, 10
public class LiteralExpr extends Expr {
    public final Object value; // Stored as an Integer

    public LiteralExpr(Object value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }
}