package optimizer;

import icg.Quadruple;
import java.util.List;

public class Optimizer {

    public List<Quadruple> optimize(List<Quadruple> code) {
        // Step 1: Constant Folding
        ConstantFolder folder = new ConstantFolder();
        List<Quadruple> folded = folder.foldConstants(code);

        // Step 2: Dead Code Elimination
        DeadCodeEliminator eliminator = new DeadCodeEliminator();

        // Return final optimized code
        return eliminator.eliminateDeadCode(folded);
    }
}
