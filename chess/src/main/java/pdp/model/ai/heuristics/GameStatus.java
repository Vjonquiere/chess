package pdp.model.ai.heuristics;

import pdp.model.board.Board;
import pdp.model.piece.Color;

/** Heuristic adding/ removing points in case of checkmate. */
public class GameStatus implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100;

  /**
   * Evaluates the board based on the possible checkmates.
   *
   * @param board Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public float evaluate(final Board board, final boolean isWhite) {
    float score = 0;
    if (board.getBoardRep().isCheckMate(Color.WHITE)) {
      score -= SCORE_CAP;
    }
    if (board.getBoardRep().isCheckMate(Color.BLACK)) {
      score += SCORE_CAP;
    }
    return isWhite ? score : -score;
  }
}
