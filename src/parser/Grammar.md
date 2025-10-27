### The following rules, written in a simplified **Backus-Naur Form (BNF)**, will serve as the "blueprint" for our `Parser.java` class.

### Notation Guide
* `->`: "is defined as"
* `|`: "or"
* `*`: "zero or more of the preceding item"
* `'text'`: A literal token lexeme (e.g., `'if'`, `'{'`).
* `UPPERCASE`: A token type from our `Lexer` (e.g., `IDENTIFIER`, `NUMBER`).

# TinyLang Official Grammar (BNF)

### 1. Program Structure

A `TinyLang` program is a sequence of zero or more statements.

```bnf
program     -> statement*
```

### 2. Statements

```bnf
statement   -> decl_stmt
             | assign_stmt
             | if_stmt
             | while_stmt
             | print_stmt
             | read_stmt
             | block_stmt

// Variable declaration (e.g., int x;)
decl_stmt   -> 'int' IDENTIFIER ';'

// Variable assignment (e.g., x = y + 5;)
assign_stmt -> IDENTIFIER '=' expression ';'

// If-else statement
// The body of 'if' and 'else' must be a single statement,
// which is typically a block_stmt.
if_stmt     -> 'if' '(' expression ')' statement 'else' statement

// While loop
while_stmt  -> 'while' '(' expression ')' statement

// Print statement (e.g., print(x);)
print_stmt  -> 'print' '(' IDENTIFIER ')' ';'

// Read statement (e.g., read(y);)
read_stmt   -> 'read' '(' IDENTIFIER ')' ';'

// A block of code { ... } which contains its own program
block_stmt  -> '{' program '}'
```

### 3. Expressions

```bnf
// Entry point for all expressions
expression  -> comparison

// Level 1: Comparison (Lowest Precedence)
// Handles: x < 5
comparison  -> term ( '<' term )*

// Level 2: Term (Addition / Subtraction)
// Handles: x + 1 or y - 5
term        -> primary ( ('+' | '-') primary )*

// Level 3: Primary (Highest Precedence)
// Defines the "atomic" parts of an expression.
primary     -> NUMBER
             | IDENTIFIER
             | '(' expression ')'
```