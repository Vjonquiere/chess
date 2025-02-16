package pdp.model.ai.algorithms;

import pdp.model.Game;
import pdp.model.ai.AIMove;

public interface SearchAlgorithm {
  AIMove findBestMove(Game game, int depth, boolean player);
}
