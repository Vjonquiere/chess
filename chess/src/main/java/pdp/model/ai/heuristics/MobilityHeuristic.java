package pdp.model.ai.heuristics;

import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Board;

/** Heuristic based on the number of moves available for each player. */
public class MobilityHeuristic implements Heuristic {
  private static final int SCORE_CAP = 100;
  private static final int MOVE_VALUE = 1;

  /**
   * Evaluates the board based on the available moves for each player.
   *
   * @param board Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public float evaluate(final Board board, final boolean isWhite) {

    // realistic maximum of moves is 100 (can be more in specific positions)

    int score = 0;
    if (board.getBoardRep() instanceof BitboardRepresentation bitBoard) {
      score +=
          (bitBoard.getColorMoveBitboard(true).bitCount()
                  - bitBoard.getColorMoveBitboard(false).bitCount())
              * MOVE_VALUE;

      score = Math.min(score, SCORE_CAP); // cap to 100
      return isWhite ? (int) (score * 1) : (int) (-score * 1);
    }

    score +=
        (board.getBoardRep().getAllAvailableMoves(isWhite).size()
                - board.getBoardRep().getAllAvailableMoves(!isWhite).size())
            * MOVE_VALUE;

    score = Math.min(score, SCORE_CAP); // cap to 100
    return isWhite ? (int) (score * 1) : (int) (-score * 1);
  }
}
