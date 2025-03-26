package pdp.model.ai.algorithms;

import pdp.model.Game;
import pdp.model.ai.AiMove;

/** Common interface for all AI algorithms to be able to change the solver's algorithm with ease. */
public interface SearchAlgorithm {
  AiMove findBestMove(Game game, int depth, boolean player);
}
