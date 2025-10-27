package semantic;

import lexer.Token;
import parser.ast.*;

import java.util.List;


public class SemanticAnalyzer implements StmtVisitor<Void>, ExprVisitor<DataType> {

    private final SymbolTable symbolTable = new SymbolTable();
    private boolean hadError = false;


    public boolean analyze(List<Stmt> statements) {
        hadError = false;
        for (Stmt statement : statements) {
            statement.accept(this);
        }
        return !hadError;
    }



    private void error(Token token, String message) {
        System.err.println("[Line " + token.lineNumber + "] Semantic Error at '" + token.lexeme + "': " + message);
        hadError = true;
    }


    @Override
    public Void visitBlockStmt(BlockStmt stmt) {
        symbolTable.enterScope();

        for (Stmt statement : stmt.statements) {
            statement.accept(this);
        }

        symbolTable.exitScope();
        return null;
    }

    @Override
    public Void visitVarDeclStmt(VarDeclStmt stmt) {
        if (symbolTable.checkCurrentScope(stmt.name.lexeme)) {
            error(stmt.name, "Variable is already declared in this scope.");
        } else {
            SymbolInfo info = new SymbolInfo(stmt.name.lexeme, DataType.INT);
            symbolTable.define(info);
        }
        return null;
    }

    @Override
    public Void visitAssignStmt(AssignStmt stmt) {
        // 1. Check if the variable being assigned to is declared
        SymbolInfo info = symbolTable.lookup(stmt.name.lexeme);
        if (info == null) {
            error(stmt.name, "Undeclared variable.");
            return null;
        }

        // 2. Visit the expression on the right to get its type
        DataType valueType = stmt.value.accept(this);

        // 3. Type Check: Check if the expression's type matches the variable's type
        if (valueType != info.type) {
            error(stmt.name, "Type mismatch. Cannot assign " + valueType + " to " + info.type + ".");
        }

        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt stmt) {
        // 1. Check the condition
        DataType conditionType = stmt.condition.accept(this);
        if (conditionType != DataType.INT) {
            // This error is hard to hit in TinyLang, but good to have
            error(new Token(null, "if-condition", -1), "If condition must be a valid expression (INT).");
        }

        // 2. Visit the branches
        stmt.thenBranch.accept(this);
        if (stmt.elseBranch != null) {
            stmt.elseBranch.accept(this);
        }

        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt stmt) {
        // 1. Check the condition
        DataType conditionType = stmt.condition.accept(this);
        if (conditionType != DataType.INT) {
            error(new Token(null, "while-condition", -1), "While condition must be a valid expression (INT).");
        }

        // 2. Visit the body
        stmt.body.accept(this);
        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStmt stmt) {
        // Check if the variable to print is declared
        if (symbolTable.lookup(stmt.name.lexeme) == null) {
            error(stmt.name, "Undeclared variable.");
        }
        return null;
    }

    @Override
    public Void visitReadStmt(ReadStmt stmt) {
        // Check if the variable to read into is declared
        if (symbolTable.lookup(stmt.name.lexeme) == null) {
            error(stmt.name, "Undeclared variable.");
        }
        return null;
    }


    @Override
    public DataType visitBinaryExpr(BinaryExpr expr) {
        // 1. Get the types of the left and right sides
        DataType leftType = expr.left.accept(this);
        DataType rightType = expr.right.accept(this);

        // 2. Type Check: In TinyLang, all binary ops (+, -, <) are between INTs
        if (leftType == DataType.INT && rightType == DataType.INT) {
            return DataType.INT; // The result of (INT op INT) is INT
        }

        // If we got here, one of them wasn't an INT
        error(expr.operator, "Operands must be of type INT.");
        return DataType.INT; // Return INT to avoid cascading errors
    }

    @Override
    public DataType visitLiteralExpr(LiteralExpr expr) {
        // The type of a number literal is always INT
        return DataType.INT;
    }

    @Override
    public DataType visitVariableExpr(VariableExpr expr) {
        // Look up the variable in the symbol table
        SymbolInfo info = symbolTable.lookup(expr.name.lexeme);
        if (info == null) {
            error(expr.name, "Undeclared variable.");
            return DataType.INT; // Return INT to avoid cascading errors
        }

        // Return the type we found in the table
        return info.type;
    }

    @Override
    public DataType visitGroupingExpr(GroupingExpr expr) {
        // The type of a grouping is just the type of the expression inside it
        return expr.expression.accept(this);
    }
}