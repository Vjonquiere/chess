package pdp.model.ai.algorithms;

import pdp.model.Game;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;

public class AlphaBeta implements SearchAlgorithm {
  Solver solver;

  public AlphaBeta(Solver solver) {
    this.solver = solver;
  }

  @Override
  public AIMove findBestMove(Game game, int depth, boolean player) {
    return null;
  }
}
