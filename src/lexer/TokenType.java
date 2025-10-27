package lexer;

public enum TokenType {
    // Keywords
    INT, IF, ELSE, WHILE, PRINT, READ,

    // Identifiers
    IDENTIFIER,

    // Constants
    NUMBER,

    // Operators
    ASSIGN,     // =
    PLUS,       // +
    MINUS,      // -
    LESS_THAN,  // <

    // Punctuation
    LPAREN,     // (
    RPAREN,     // )
    LBRACE,     // {
    RBRACE,     // }
    SEMICOLON,  // ;

    // Utility
    EOF,
    UNKNOWN
}