package pdp.model.ai.heuristics;

import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

/**
 * Heuristic based on the balance of kings position. If they are in opposition , the endgame tends
 * to lead to a draw.
 */
public class KingOppositionHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100;

  /** Score penalty added when the kings are in opposition. */
  private static final float OPPOSITION_SCORE = -SCORE_CAP;

  /** Score penalty added when the kings are diagonally close. */
  private static final float DIAGONAL_SCORE = -(SCORE_CAP / 2);

  /**
   * Computes a score according to the (un)balance of the kings position. The more the kings are in
   * opposition, the more likely it is for the endgame to be drawish. Heuristic used for endgames.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score depending on the progress of the pawns
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    int score = 0;
    score += evaluateKingOpposition(board);

    // min score -100 (max 0)

    return score;
  }

  /**
   * Checks king opposition for endgames. If the kings are in opposition (same file, rank, or
   * diagonal with a small distance), the position is more drawish. Else, more imbalance but cannot
   * know who's got the advantage.
   *
   * @param board the board of the game
   * @return a score based on the king opposition
   */
  private float evaluateKingOpposition(final BoardRepresentation board) {
    final Position whiteKing = board.getKing(true).get(0);
    final Position blackKing = board.getKing(false).get(0);

    final int diffX = Math.abs(whiteKing.x() - blackKing.x());
    final int diffY = Math.abs(whiteKing.y() - blackKing.y());

    // If kings are directly opposite with one square between them
    if ((diffX == 2 && diffY == 0) || (diffY == 2 && diffX == 0)) {
      // Strong opposition so more drawish
      return OPPOSITION_SCORE;
    }
    // If kings are diagonally close
    if (diffX <= 2 && diffY <= 2) {
      // Marginally drawish cuz slight opposition
      return DIAGONAL_SCORE;
    }
    // No real opposition
    return 0;
  }
}
