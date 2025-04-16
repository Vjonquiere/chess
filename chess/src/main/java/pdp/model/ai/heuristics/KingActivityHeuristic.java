package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

/** Heuristic based on the activity of the king. */
public class KingActivityHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  /** Score obtained when the king has more than five moves available. */
  private static final float ACTIVITY_SCORE = 10f;

  /** Score obtained when the king is in the center of the board. */
  private static final float CENTER_SCORE = 20f;

  /** Score obtained when the king is not in the center. */
  private static final float NOT_CENTER_MAX = 15f;

  /** Score penalty calculated based on the distance between the king and the center. */
  private static final float CENTER_DISTANCE_DECREASE = 3f;

  /** Minimum number of available moves of the king to be considered active. */
  private static final int MOVES_ACTIVE_KING = 5;

  /** The multiplier used to keep the values under SCORE_CAP. */
  private static final float MULTIPLIER = SCORE_CAP / (ACTIVITY_SCORE + CENTER_SCORE);

  /**
   * Checks the activity of the king and returns a score accordingly. King is close to the center?
   * King has a lot of possible moves ?
   *
   * @param board board of the game
   * @param isWhite true if this is for white, false otherwise
   * @return score according to the activity of the king
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    int score = 0;
    score += kingIsInCenterScore(board, true) - kingIsInCenterScore(board, false);
    score += kingActivityScore(board, true) - kingActivityScore(board, false);

    // max score 30

    score *= MULTIPLIER;

    return isWhite ? score : -score;
  }

  /**
   * Checks the location of the king and returns a score accordingly.
   *
   * @param board board of the game
   * @param isWhite true if this is for white, false otherwise
   * @return score according to the location of the king on the board
   */
  private float kingIsInCenterScore(final BoardRepresentation board, final boolean isWhite) {
    final float score;

    // Delineate center box
    final Position posTopLeftCenter = new Position(2, 5);
    final Position posTopRightCenter = new Position(5, 5);
    final Position posDownLeftCenter = new Position(2, 2);
    final Position posDownRightCenter = new Position(5, 2);

    final Position kingPosition = board.getKing(isWhite).get(0);
    // Check if the king is close to the center of the board and therefore has easier access to the
    // entire board
    final boolean isKingInCenter =
        kingPosition.x() >= posDownLeftCenter.x()
            && kingPosition.x() <= posTopRightCenter.x()
            && kingPosition.y() >= posDownRightCenter.y()
            && kingPosition.y() <= posTopLeftCenter.y();

    if (isKingInCenter) {
      score = CENTER_SCORE;
    } else {
      // Compute Manhattan distance to center
      final int centerX = (posTopLeftCenter.x() + posTopRightCenter.x()) / 2;
      final int centerY = (posDownLeftCenter.y() + posTopLeftCenter.y()) / 2;

      final int distance =
          Math.abs(kingPosition.x() - centerX) + Math.abs(kingPosition.y() - centerY);
      final int noBonus = 0;
      // King more or less far from the center
      score = Math.max(noBonus, NOT_CENTER_MAX - (distance * CENTER_DISTANCE_DECREASE));
    }

    return score;
  }

  /**
   * Checks the activity of the king and returns a score accordingly.
   *
   * @param board board of the game
   * @param isWhite true if this is for white, false otherwise
   * @return score according to the activity of the king
   */
  private float kingActivityScore(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    // Check the activity of the King
    final List<Move> kingMoves = board.retrieveKingMoves(isWhite);
    if (kingMoves.size() >= MOVES_ACTIVE_KING) {
      score += ACTIVITY_SCORE;
    }

    return score;
  }
}
