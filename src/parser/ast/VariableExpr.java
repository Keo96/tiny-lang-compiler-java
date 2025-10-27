package parser.ast;

import lexer.Token;

// Represents a variable name being used: x, y
public class VariableExpr extends Expr {
    public final Token name;

    public VariableExpr(Token name) {
        this.name = name;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitVariableExpr(this);
    }
}