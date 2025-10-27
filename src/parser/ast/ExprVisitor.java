package parser.ast;

// The "Visitor" interface for all expression types.
public interface ExprVisitor<R> {
    R visitBinaryExpr(BinaryExpr expr);
    R visitLiteralExpr(LiteralExpr expr);
    R visitVariableExpr(VariableExpr expr);
    R visitGroupingExpr(GroupingExpr expr);
}