package pdp.model.ai.algorithms;

import pdp.model.Game;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;

public class MCTS implements SearchAlgorithm {
  Solver solver;
  private static final double EXPLORATION_FACTOR = Math.sqrt(2); // c value

  public MCTS(Solver solver) {
    this.solver = solver;
  }

  @Override
  public AIMove findBestMove(Game game, int depth, boolean player) {
    return null;
  }

  /**
   * Select the node to explore
   *
   * @param node the current tree node in the algorithm
   * @return the node that the algorithm select to explore (based on UCT)
   */
  private TreeNodeMCTS select(TreeNodeMCTS node) {
    while (!node.getChildrenNodes().isEmpty()) {
      node = node.getChildToExplore(EXPLORATION_FACTOR);
    }
    return node;
  }

  // Expansion

  // Simulation

  /**
   * Back propagates the obtained result during the algorithm
   *
   * @param node the current tree node in the algorithm
   * @param result the obtained result after simulation
   */
  private void backpropagate(TreeNodeMCTS node, int result) {
    while (node != null) {
      node.incrementNbVisits();
      node.incrementNbWinsBy(result);
      node = node.getParentNode();
    }
  }
}
