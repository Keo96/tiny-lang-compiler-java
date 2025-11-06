import icg.Quadruple;
import icg.TacGenerator;
import lexer.Lexer;
import lexer.Token;
import optimizer.Optimizer;
import parser.Parser;
import parser.ast.Stmt;
import parser.AstPrinter;
import semantic.SemanticAnalyzer;
import target.TargetCodeGeneration;

import java.util.List;

public class Main {
    static boolean hadError = false; // Global error flag
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

        // --- 1. LEXICAL ANALYSIS ---
        System.out.println("Scanning source code...");
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.scanTokens();

        System.out.println("Found " + tokens.size() + " tokens:");
        for (Token token : tokens) {
            System.out.println(token);
        }

        // --- 2. SYNTAX ANALYSIS ---
        System.out.println("\nParsing tokens into AST...");
        Parser parser = new Parser(tokens);
        List<Stmt> ast = parser.parse();

        System.out.println("\nGenerated Abstract Syntax Tree (AST):");
        AstPrinter printer = new AstPrinter();
        System.out.println(printer.print(ast));

        // --- 3. SEMANTIC ANALYSIS  ---
        System.out.println("\nAnalyzing semantics (checking logic)...");
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        boolean passedSemanticAnalysis = analyzer.analyze(ast);

        if (!passedSemanticAnalysis) {
            System.err.println("Semantic errors found. Halting compilation.");
            hadError = true; // Set our main error flag
        } else {
            System.out.println("Semantic analysis passed.");
        }

        // --- STOP COMPILATION IF THERE WAS ANY ERROR ---
        if (hadError) {
            System.out.println("\nCompilation failed.");
            return; // Exit the program
        }

        // --- 4. INTERMEDIATE CODE GENERATION ---
        System.out.println("\nGenerating Three-Address Code...");
        TacGenerator tacGen = new TacGenerator();
        List<Quadruple> quads = tacGen.generate(ast);

        // Print the formatted table
        System.out.println("\n--- Three-Address Code (Quadruples) ---");
        System.out.printf("%-10s | %-10s | %-10s | %-10s\n",
                "Op", "Arg1", "Arg2", "Result");
        System.out.println("-----------------------------------------------------");
        for (Quadruple quad : quads) {
            System.out.println(quad);
        }

        // --- 5. CODE OPTIMIZATION ---
        System.out.println("\nOptimizing Three-Address Code...");
        Optimizer optimizer = new Optimizer();
        List<Quadruple> optimizedQuads = optimizer.optimize(quads);

        // Print the optimized TAC
        System.out.println("\n--- Optimized Three-Address Code ---");
        System.out.printf("%-10s | %-10s | %-10s | %-10s\n",
                "Op", "Arg1", "Arg2", "Result");
        System.out.println("-----------------------------------------------------");
        for (Quadruple quad : optimizedQuads) {
            System.out.println(quad);
        }
        // --- 6. TARGET CODE GENERATION ---
        System.out.println("\nGenerating target stack-based assembly...");
        TargetCodeGeneration targetGen = new TargetCodeGeneration();
        List<String> assembly = targetGen.generate(optimizedQuads);

        System.out.println("\n--- Target Stack-Based Assembly ---");
        assembly.forEach(System.out::println);
    }
}