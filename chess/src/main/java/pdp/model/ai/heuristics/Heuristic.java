package pdp.model.ai.heuristics;

import pdp.model.board.Board;

/** Common interface for heuristics,to be able to change the solver's heuristic with ease. */
public interface Heuristic {
  /**
   * computes the score of a board with the heuristic implementing the class, depends on the player.
   *
   * @param board Board to evaluate
   * @param isWhite true if the player is white, false otherwise
   * @return score for the given player on the board
   */
  float evaluate(Board board, boolean isWhite);
}
