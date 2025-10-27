package parser.ast;

public interface StmtVisitor<R> {
    R visitBlockStmt(BlockStmt stmt);
    R visitVarDeclStmt(VarDeclStmt stmt);
    R visitAssignStmt(AssignStmt stmt);
    R visitIfStmt(IfStmt stmt);
    R visitWhileStmt(WhileStmt stmt);
    R visitPrintStmt(PrintStmt stmt);
    R visitReadStmt(ReadStmt stmt);
}