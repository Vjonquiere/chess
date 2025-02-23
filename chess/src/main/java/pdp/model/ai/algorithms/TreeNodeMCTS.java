package pdp.model.ai.algorithms;

import java.util.ArrayList;
import java.util.List;
import pdp.model.GameState;

/**
 * Node in the MCTS algorithm. Contains GameState, parent node (for backpropagation and other
 * operations), children nodes (to go down the tree), the number of registered wins and the number
 * of visits for this node.
 */
public class TreeNodeMCTS {
  private GameState state;
  private TreeNodeMCTS parent;
  private List<TreeNodeMCTS> children;
  private int wins;
  private int nbVisits;

  public TreeNodeMCTS(GameState state, TreeNodeMCTS parent) {
    this.state = state;
    this.parent = parent;
    this.children = new ArrayList<>();
    this.wins = 0;
    this.nbVisits = 0;
  }
}
