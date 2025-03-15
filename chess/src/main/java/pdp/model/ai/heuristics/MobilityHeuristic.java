package pdp.model.ai.heuristics;

import pdp.model.board.BitboardRepresentation;
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
    int score = 0;
    if (board.getBoardRep() instanceof BitboardRepresentation bitBoard) {
      score +=
          bitBoard.getColorMoveBitboard(true).bitCount()
              - bitBoard.getColorMoveBitboard(false).bitCount();
      return isWhite ? (int) (score * 0.1) : (int) (-score * 0.1);
    }

    return (int)
        ((board.getBoardRep().getAllAvailableMoves(isWhite).size()
                - board.getBoardRep().getAllAvailableMoves(!isWhite).size())
            * 0.1);
  }

  @Override
  public boolean isThreefoldImpact() {
    return false;
  }
}
