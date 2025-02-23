package pdp.model.ai.algorithms;

import pdp.model.Game;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;

public class MCTS implements SearchAlgorithm {
  Solver solver;

  public MCTS(Solver solver) {
    this.solver = solver;
  }

  @Override
  public AIMove findBestMove(Game game, int depth, boolean player) {
    return null;
  }

  // Selection

  // Expansion

  // Simulation

  /**
   * Back propagates the obtained result during the algorithm
   *
   * @param node the current tree node in the algorithm
   * @param result the onbtained result after simulation
   */
  private void backpropagate(TreeNodeMCTS node, int result) {
    while (node != null) {
      node.incrementNbVisits();
      node.incrementNbWinsBy(result);
      node = node.getParentNode();
    }
  }
}
