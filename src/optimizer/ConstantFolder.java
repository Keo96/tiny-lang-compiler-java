package optimizer;

import icg.Quadruple;
import java.util.ArrayList;
import java.util.List;

public class ConstantFolder {

    public List<Quadruple> foldConstants(List<Quadruple> code) {
        List<Quadruple> optimized = new ArrayList<>();

        for (Quadruple q : code) {
            String op = q.op();
            String a1 = q.arg1();
            String a2 = q.arg2();
            String res = q.result();

            // Check if both arguments are constants (numbers)
            if (isNumeric(a1) && isNumeric(a2) && (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("<"))) {
                int val1 = Integer.parseInt(a1);
                int val2 = Integer.parseInt(a2);
                int foldedValue = switch (op) {
                    case "+" -> val1 + val2;
                    case "-" -> val1 - val2;
                    case "*" -> val1 * val2;
                    case "<" -> (val1 < val2) ? 1 : 0;
                    default -> 0;
                };

                optimized.add(new Quadruple("=", String.valueOf(foldedValue), null, res));
            } else {
                optimized.add(q);
            }
        }

        return optimized;
    }

    private boolean isNumeric(String s) {
        if (s == null) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
