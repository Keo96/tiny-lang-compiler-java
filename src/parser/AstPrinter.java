package parser;

import parser.ast.*;

// This class implements our Visitor interfaces to print the tree.
public class AstPrinter implements StmtVisitor<String>, ExprVisitor<String> {

    // Main method to print a list of statements (our program)
    public String print(java.util.List<Stmt> statements) {
        StringBuilder builder = new StringBuilder();
        for (Stmt statement : statements) {
            builder.append(statement.accept(this)).append("\n");
        }
        return builder.toString();
    }

    // Helper method to create a parenthesized string
    private String parenthesize(String name, Object... parts) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Object part : parts) {
            builder.append(" ");
            if (part instanceof Stmt) {
                builder.append(((Stmt) part).accept(this));
            } else if (part instanceof Expr) {
                builder.append(((Expr) part).accept(this));
            } else {
                builder.append(part.toString());
            }
        }
        builder.append(")");
        return builder.toString();
    }

    // --- Statement Visitor Methods ---

    @Override
    public String visitBlockStmt(BlockStmt stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(block \n");
        for (Stmt statement : stmt.statements) {
            builder.append("  ").append(statement.accept(this)).append("\n");
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitVarDeclStmt(VarDeclStmt stmt) {
        return parenthesize("var-decl", stmt.name.lexeme);
    }

    @Override
    public String visitAssignStmt(AssignStmt stmt) {
        return parenthesize("=", stmt.name.lexeme, stmt.value);
    }

    @Override
    public String visitIfStmt(IfStmt stmt) {
        if (stmt.elseBranch == null) {
            return parenthesize("if", stmt.condition, stmt.thenBranch);
        }
        return parenthesize("if-else", stmt.condition, stmt.thenBranch, stmt.elseBranch);
    }

    @Override
    public String visitWhileStmt(WhileStmt stmt) {
        return parenthesize("while", stmt.condition, stmt.body);
    }

    @Override
    public String visitPrintStmt(PrintStmt stmt) {
        return parenthesize("print", stmt.name.lexeme);
    }

    @Override
    public String visitReadStmt(ReadStmt stmt) {
        return parenthesize("read", stmt.name.lexeme);
    }

    // --- Expression Visitor Methods ---

    @Override
    public String visitBinaryExpr(BinaryExpr expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitLiteralExpr(LiteralExpr expr) {
        return expr.value.toString();
    }

    @Override
    public String visitVariableExpr(VariableExpr expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitGroupingExpr(GroupingExpr expr) {
        return parenthesize("group", expr.expression);
    }
}