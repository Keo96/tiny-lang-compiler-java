import lexer.Lexer;
import lexer.Token;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Your example code from Project.pdf [cite: 289-307]
        String sourceCode = "int x;\n" +
                "int y;\n" +
                "x = y + 5;\n" +
                "if (x < 5) {\n" +
                "  x = x + 1;\n" +
                "} else {\n" +
                "  x = x - 1;\n" +
                "}\n" +
                "while (x < 10) {\n" +
                "  x = x + 1;\n" +
                "}\n" +
                "print(x);\n" +
                "read(y);";

        System.out.println("Scanning source code...");
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.scanTokens();

        System.out.println("Found " + tokens.size() + " tokens:");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}