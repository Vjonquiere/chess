package pdp.model.ai.algorithms;

import pdp.model.Game;
import pdp.model.ai.AiMove;

/** Common interface for all AI algorithms to be able to change the solver's algorithm with ease. */
public interface SearchAlgorithm {
  /**
   * Determines the best move using the implemented algorithm.
   *
   * @param game The current game state.
   * @param depth The number of moves to look ahead.
   * @param player The current player (true for white, false for black).
   * @return The best move for the player.
   */
  AiMove findBestMove(Game game, int depth, boolean player);
}
