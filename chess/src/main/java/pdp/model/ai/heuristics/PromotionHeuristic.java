package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

/** Heuristic based on the closeness of pawn promotion. */
public class PromotionHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  /** Bonus score added when pawns are one step away from promotion. */
  private static final float SECOND_LAST_RANK_SCORE = 20f;

  /** Bonus score added when pawns are close to promotion. */
  private static final float FINAL_PHASE_SCORE = 10f;

  /** Bonus score added when a pawn has progressed. */
  private static final float PROGRESS_SCORE = 10f;

  /** The multiplier used to keep the values under SCORE_CAP. */
  private static final float MULTIPLIER =
      SCORE_CAP / (8 * SECOND_LAST_RANK_SCORE + 8 * PROGRESS_SCORE);

  /**
   * Computes a score according to the closeness of pawns promoting. Heuristic used for endgames.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score depending on the progress of the pawns
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    score += pawnsHaveProgressedScore(board, true) - pawnsHaveProgressedScore(board, false);
    score += pawnsAreCloseToPromotion(board, true) - pawnsAreCloseToPromotion(board, false);

    score *= MULTIPLIER;

    return isWhite ? score : -score;
  }

  /**
   * Checks if the pawns are well advanced for the corresponding color and returns a score
   * accordingly.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score if the pawns were pushed far enough for the majority of them
   */
  private float pawnsHaveProgressedScore(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    if (board.pawnsHaveProgressed(isWhite)) {
      score += PROGRESS_SCORE;
    }

    return score;
  }

  /**
   * Evaluates how close pawns are to promotion.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on how many pawns are close to promoting.
   */
  private float pawnsAreCloseToPromotion(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;

    final List<Position> pawns = board.getPawns(isWhite);

    final int secondLastRank = isWhite ? 6 : 1;

    for (final Position pawn : pawns) {
      if (pawn.y() == secondLastRank) {
        // Pawn one step from promotion
        score += SECOND_LAST_RANK_SCORE;
      } else if ((isWhite && pawn.y() >= 5) || (!isWhite && pawn.y() <= 2)) {
        // Pawn in the final phase of advancement
        score += FINAL_PHASE_SCORE;
      }
    }

    return score;
  }
}
