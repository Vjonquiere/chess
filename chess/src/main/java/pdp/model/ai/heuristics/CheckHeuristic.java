package pdp.model.ai.heuristics;

import pdp.model.board.BoardRepresentation;
import pdp.model.piece.Color;

/** Heuristic adding/ removing points in case of check. */
public class CheckHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100;

  /**
   * Evaluates the board based on the possible check.
   *
   * @param board Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    if (board.isCheck(Color.WHITE)) {
      score -= SCORE_CAP;
    }
    if (board.isCheck(Color.BLACK)) {
      score += SCORE_CAP;
    }
    return isWhite ? score : -score;
  }
}
