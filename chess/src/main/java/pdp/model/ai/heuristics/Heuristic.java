package pdp.model.ai.heuristics;

import pdp.model.Game;

public interface Heuristic {
  int evaluate(Game game, boolean isWhite);
}
