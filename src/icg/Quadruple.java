package icg;

/**
 * Represents a single Three-Address Code (TAC) instruction as a Quadruple.
 */
public record Quadruple(String op, String arg1, String arg2, String result) {


    @Override
    public String toString() {
        // Use "---" or "null" for any null field for clarity
        String arg1Str = (arg1 == null) ? "---" : arg1;
        String arg2Str = (arg2 == null) ? "---" : arg2;
        String resultStr = (result == null) ? "---" : result;

        // "%-10s" means "left-aligned, 10-character wide string"
        return String.format("%-10s | %-10s | %-10s | %-10s",
                op, arg1Str, arg2Str, resultStr);
    }
}