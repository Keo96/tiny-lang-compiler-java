package semantic;

/**
 * Stores information about a declared symbol in the symbol table.
 */
public class SymbolInfo {

    /**
     * The data type of the symbol, e.g., INT.
     */
    public final String name;
    public final DataType type;

    public SymbolInfo(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Symbol(name=" + name + ", type=" + type + ")";
    }
}