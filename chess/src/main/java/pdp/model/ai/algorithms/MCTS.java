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

  // Selection

  // Expansion

  // Simulation

  // BackPropagation

  // To know which node to visit next, use UCT (Upper Confidence bound applied to Trees).
  // w(i)/n(i) + c * sqrt(ln(t)/n(i))

  // w = number of wins after the i-th move (can be 0)
  // n = number of simulations after the i-th move
  // c = exploration parameter
  // t = total number of simulations for the parent node

  // Basically we want to retain some aspect of "history" in the decision making
  // And expand the exploration of possibilities
}
