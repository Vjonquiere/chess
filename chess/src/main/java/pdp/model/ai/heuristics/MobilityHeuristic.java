package pdp.model.ai.heuristics;

import pdp.model.board.Board;

public class MobilityHeuristic implements Heuristic {
  /**
   * Evaluates the board based on the available moves for each player. The amount is divided by 10
   * based on Shannon's paper (XXII. Programming a Computer for Playing Chess).
   *
   * @param board Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public int evaluate(Board board, boolean isWhite) {
    return (int)
        ((board.getBoardRep().getAllAvailableMoves(isWhite).size()
                - board.getBoardRep().getAllAvailableMoves(!isWhite).size())
            * 0.1);
  }
}
