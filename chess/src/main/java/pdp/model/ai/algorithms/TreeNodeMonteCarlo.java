package pdp.model.ai.algorithms;

import java.util.ArrayList;
import java.util.List;
import pdp.model.GameState;
import pdp.model.board.Move;

/**
 * Node in the MonteCarloTreeSearch algorithm. Contains GameState, parent node (for backpropagation
 * and other operations), children nodes (to go down the tree), the number of registered wins and
 * the number of visits for this node.
 */
public class TreeNodeMonteCarlo {
  private GameState state;
  private TreeNodeMonteCarlo parent;
  private List<TreeNodeMonteCarlo> children;
  private int wins;
  private int nbVisits;
  private Move startingMove;

  public TreeNodeMonteCarlo(GameState state, TreeNodeMonteCarlo parent, Move move) {
    this.state = state;
    this.parent = parent;
    this.children = new ArrayList<>();
    this.wins = 0;
    this.nbVisits = 0;
    this.startingMove = move;
  }

  /**
   * @return the number of wins that were observed
   */
  public int getNbWins() {
    return this.wins;
  }

  /**
   * @return the starting move that leads to other game states
   */
  public Move getStartingMove() {
    return this.startingMove;
  }

  /**
   * Increments the number of registered wins by {nbNewWins}
   *
   * @param nbNewWins the number of new wins
   */
  public void incrementNbWinsBy(int nbNewWins) {
    this.wins += nbNewWins;
  }

  /**
   * @return the number of times this node was visited
   */
  public int getNbVisits() {
    return this.nbVisits;
  }

  /** Increments the number of visits by 1 */
  public void incrementNbVisits() {
    this.nbVisits++;
  }

  /**
   * @return the list of children nodes for the current node
   */
  public List<TreeNodeMonteCarlo> getChildrenNodes() {
    return this.children;
  }

  /**
   * @return the parent node of the current node
   */
  public TreeNodeMonteCarlo getParentNode() {
    return this.parent;
  }

  /**
   * @return the GameState representing the current node
   */
  public GameState getGameState() {
    return this.state;
  }

  /**
   * Calculate UCT (Upper Confidence Bound for Trees)
   *
   * <p>UCT = w(i)/n(i) + c * sqrt(ln(t)/n(i))
   *
   * <p>w = number of wins after the i-th move (can be 0). n = number of simulations after the i-th
   * move. c = exploration parameter. t = total number of simulations for the parent node.
   *
   * @param node the child node
   * @param exploration the exploration factor (c value in formula)
   * @return The UCT value
   */
  private double uctValue(TreeNodeMonteCarlo node, double exploration) {
    if (node.nbVisits == 0) {
      return Double.MAX_VALUE;
    }
    return (node.wins / (double) node.nbVisits)
        + exploration * Math.sqrt(Math.log(this.nbVisits) / node.nbVisits);
  }

  /**
   * Adds a child node to the current node (expand tree)
   *
   * @param child the child node wer want to add to the tree
   */
  public void addChildToTree(TreeNodeMonteCarlo child) {
    children.add(child);
  }

  /**
   * Return the best child based on the UCT formula
   *
   * @param exploration the exploration parameter (c value in formula)
   * @return the best child node in the tree (current node is root)
   */
  public TreeNodeMonteCarlo getChildToExplore(double exploration) {
    TreeNodeMonteCarlo bestChild = null;
    // First -inf and upload it later when better child is found
    double bestValue = Double.NEGATIVE_INFINITY;

    for (TreeNodeMonteCarlo child : this.children) {
      double uct = uctValue(child, exploration);
      if (uct > bestValue) {
        // Upload best child
        bestValue = uct;
        bestChild = child;
      }
    }

    return bestChild;
  }

  /**
   * Tells if a node is fully explored
   *
   * @return true if a node is fully explored, false otherwise
   */
  public boolean isFullyExpanded() {
    boolean isWhite = state.isWhiteTurn();
    return state.getBoard().getBoardRep().getAllAvailableMoves(isWhite).size() == children.size();
  }
}
