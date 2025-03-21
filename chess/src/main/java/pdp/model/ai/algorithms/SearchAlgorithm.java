package pdp.model.ai.algorithms;

import pdp.model.Game;
import pdp.model.ai.AiMove;

public interface SearchAlgorithm {
  AiMove findBestMove(Game game, int depth, boolean player);
}
