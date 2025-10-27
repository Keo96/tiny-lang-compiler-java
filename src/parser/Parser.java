package parser;

import lexer.Token;
import lexer.TokenType;
import parser.ast.*; // Import all our new AST classes

import java.util.ArrayList;
import java.util.List;

public class Parser {

    // A simple class for reporting parsing errors
    private static class ParseError extends RuntimeException {
        ParseError(Token token, String message) {
            super("[Line " + token.lineNumber + "] Error at '" + token.lexeme + "': " + message);
        }
    }

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(statement());
        }
        return statements;
    }

    private Stmt statement() {
        if (match(TokenType.INT)) {
            return declaration();
        }
        if (match(TokenType.IF)) {
            return ifStatement();
        }
        if (match(TokenType.WHILE)) {
            return whileStatement();
        }
        if (match(TokenType.PRINT)) {
            return printStatement();
        }
        if (match(TokenType.READ)) {
            return readStatement();
        }
        if (match(TokenType.LBRACE)) {
            // A { starts a new block
            return new BlockStmt(block());
        }

        // If it's none of the above, it must be an assignment
        return assignmentStatement();
    }


    private Stmt declaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name after 'int'.");
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new VarDeclStmt(name);
    }

    private Stmt ifStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'if'.");
        Expr condition = expression(); // We will write this next!
        consume(TokenType.RPAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return new IfStmt(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'while'.");
        Expr condition = expression(); // We will write this next!
        consume(TokenType.RPAREN, "Expect ')' after while condition.");
        Stmt body = statement();

        return new WhileStmt(condition, body);
    }

    private Stmt printStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'print'.");
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name to print.");
        consume(TokenType.RPAREN, "Expect ')' after variable name.");
        consume(TokenType.SEMICOLON, "Expect ';' after print statement.");
        return new PrintStmt(name);
    }

    private Stmt readStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'read'.");
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name to read into.");
        consume(TokenType.RPAREN, "Expect ')' after variable name.");
        consume(TokenType.SEMICOLON, "Expect ';' after read statement.");
        return new ReadStmt(name);
    }

    private Stmt assignmentStatement() {
        // We don't use match() here because we already saw the IDENTIFIER
        // when we were in the statement() router method.
        // So we just grab the token...
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name for assignment.");

        consume(TokenType.ASSIGN, "Expect '=' after variable name.");
        Expr value = expression(); // We will write this next!
        consume(TokenType.SEMICOLON, "Expect ';' after assignment.");

        return new AssignStmt(name, value);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(statement());
        }

        consume(TokenType.RBRACE, "Expect '}' after block.");
        return statements;
    }



    private Expr expression() {
        return comparison();
    }

    private Expr comparison() {
        Expr expr = term(); // Get the left-hand 'term'

        // Loop as long as we see a '<'
        while (match(TokenType.LESS_THAN)) {
            Token operator = previous();
            Expr right = term();
            // Wrap the left expr and the new right expr in a BinaryExpr
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = primary(); // Get the left-hand 'primary'

        // Loop as long as we see a '+' or '-'
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = primary();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr primary() {
        if (match(TokenType.NUMBER)) {
            // Convert the number string to an Integer
            Object value = Integer.parseInt(previous().lexeme);
            return new LiteralExpr(value);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new VariableExpr(previous());
        }

        if (match(TokenType.LPAREN)) {
            Expr expr = expression(); // Recursive call!
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return new GroupingExpr(expr);
        }

        // If we get here, we don't know what this token is.
        throw new ParseError(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw new ParseError(peek(), message);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance(); // Consume the token
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}