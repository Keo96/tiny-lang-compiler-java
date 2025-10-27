package semantic;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {

    private final Stack<Map<String, SymbolInfo>> scopes;

    public SymbolTable() {
        this.scopes = new Stack<>();
        enterScope(); // Create the global scope
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }


    public void define(SymbolInfo info) {
        if (scopes.isEmpty()) {
            return; // No scope to define the symbol in
        }
        // Add the symbol to the map that is on top of the stack.
        scopes.peek().put(info.name, info);
    }

    public SymbolInfo lookup(String name) {

        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, SymbolInfo> scope = scopes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        // Not found in any scope
        return null;
    }

    /**
     * Checks if a symbol is defined *only* in the current (top) scope.
     * This is used to detect "variable already declared" errors.
     */
    public boolean checkCurrentScope(String name) {
        if (scopes.isEmpty()) {
            return false;
        }
        return scopes.peek().containsKey(name);
    }
}