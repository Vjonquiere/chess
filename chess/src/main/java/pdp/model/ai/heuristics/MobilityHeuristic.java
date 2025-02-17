package pdp.model.ai.heuristics;

import pdp.model.Game;

public class MobilityHeuristic implements Heuristic {
  /**
   * Evaluates the board based on the available moves for each player. The amount is divided by 10
   * based on Shannon's paper (XXII. Programming a Computer for Playing Chess).
   *
   * @param game Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public int evaluate(Game game, boolean isWhite) {
    return (int)
        ((game.getBoard().getBoardRep().getAllAvailableMoves(isWhite).size()
                - game.getBoard().getBoardRep().getAllAvailableMoves(!isWhite).size())
            * 0.1);
  }
}
