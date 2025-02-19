package pdp.model.ai.algorithms;

import pdp.model.Game;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;

public class MCTS implements SearchAlgorithm {
  Solver solver;

  public MCTS(Solver solver) {
    this.solver = solver;
  }

  @Override
  public AIMove findBestMove(Game game, int depth, boolean player) {
    return null;
  }
}
