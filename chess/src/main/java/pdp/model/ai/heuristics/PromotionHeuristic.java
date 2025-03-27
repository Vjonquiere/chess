package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

/** Heuristic based on the closeness of pawn promotion. */
public class PromotionHeuristic implements Heuristic {
  private static final int SCORE_CAP = 100;
  private static final int SECOND_LAST_RANK_SCORE = 20;
  private static final int FINAL_PHASE_SCORE = 10;
  private static final int PROGRESS_SCORE = 10;

  private static final float MULTIPLIER =
      (SCORE_CAP / (8 * SECOND_LAST_RANK_SCORE + 8 * PROGRESS_SCORE));

  /**
   * Computes a score according to the closeness of pawns promoting. Heuristic used for endgames.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score depending on the progress of the pawns
   */
  @Override
  public float evaluate(final Board board, final boolean isWhite) {
    int score = 0;
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
  private int pawnsHaveProgressedScore(final Board board, final boolean isWhite) {
    int score = 0;
    if (board.getBoardRep().pawnsHaveProgressed(isWhite)) {
      score += 10;
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
  private int pawnsAreCloseToPromotion(final Board board, final boolean isWhite) {
    int score = 0;

    final BoardRepresentation bitboard = board.getBoardRep();
    final List<Position> pawns = bitboard.getPawns(isWhite);

    final int secondLastRank = isWhite ? 6 : 1;

    for (final Position pawn : pawns) {
      if (pawn.y() == secondLastRank) {
        // Pawn one step from promotion
        score += 20;
      } else if ((isWhite && pawn.y() >= 5) || (!isWhite && pawn.y() <= 2)) {
        // Pawn in the final phase of advancement
        score += 10;
      }
    }

    return score;
  }
}
