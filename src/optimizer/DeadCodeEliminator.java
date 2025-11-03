package optimizer;

import icg.Quadruple;
import java.util.*;

public class DeadCodeEliminator {

    public List<Quadruple> eliminateDeadCode(List<Quadruple> code) {
        Set<String> live = new HashSet<>();

        // Step 1: Find the final "live" results
        for (Quadruple q : code) {
            if (q.op().equals("PRINT") || q.op().equals("READ")) {
                if (q.arg1() != null) live.add(q.arg1());
            }
        }

        // Step 2: Traverse backward and collect only instructions that affect live vars
        List<Quadruple> optimized = new ArrayList<>();

        for (int i = code.size() - 1; i >= 0; i--) {
            Quadruple q = code.get(i);
            String res = q.result();

            if (res != null && live.contains(res)) {
                optimized.add(q);
                if (q.arg1() != null) live.add(q.arg1());
                if (q.arg2() != null) live.add(q.arg2());
            } else if (q.op().equals("PRINT") || q.op().equals("READ") || q.op().startsWith("LABEL") || q.op().startsWith("IFZ") || q.op().startsWith("GOTO")) {
                optimized.add(q); // Keep control flow and I/O
                if (q.arg1() != null) live.add(q.arg1());
                if (q.arg2() != null) live.add(q.arg2());
            }
        }

        Collections.reverse(optimized);
        return optimized;
    }
}
