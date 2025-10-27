package lexer;

public class Token {
    public final TokenType type;
    public final String lexeme;
    public final int lineNumber;

    public Token(TokenType type, String lexeme, int lineNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "Token [Type: " + type + ", Lexeme: '" + lexeme + "']";
    }
}