package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

/** Heuristic based on the activity of the king. */
public class KingActivityHeuristic implements Heuristic {

  private static final float SCORE_CAP = 100f;
  private static final float ACTIVITY_SCORE = 10f;
  private static final float CENTER_SCORE = 20f;
  private static final float NOT_CENTER_MAX = 15f;
  private static final float CENTER_DISTANCE_DECREASE = 3f;
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
  public float evaluate(final Board board, final boolean isWhite) {
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
  private float kingIsInCenterScore(final Board board, final boolean isWhite) {
    final float score;

    // Delineate center box
    final Position posTopLeftCenter = new Position(2, 5);
    final Position posTopRightCenter = new Position(5, 5);
    final Position posDownLeftCenter = new Position(2, 2);
    final Position posDownRightCenter = new Position(5, 2);

    final Position kingPosition = board.getBoardRep().getKing(isWhite).get(0);
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
  private float kingActivityScore(final Board board, final boolean isWhite) {
    float score = 0;
    final BoardRepresentation bitboard = board.getBoardRep();
    // Check the activity of the King
    final List<Move> kingMoves = bitboard.retrieveKingMoves(isWhite);
    if (kingMoves.size() >= 5) {
      score += ACTIVITY_SCORE;
    }

    return score;
  }
}
