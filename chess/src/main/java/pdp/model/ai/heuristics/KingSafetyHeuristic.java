package pdp.model.ai.heuristics;

import pdp.model.board.BoardRepresentation;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

/**
 * Heuristic based on the safety of the king (not in center, pieces around to protect him, neighbors
 * attacked).
 */
public class KingSafetyHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  /** Score obtained for each piece protecting the king. */
  private static final float PROTECTION_BONUS = 5f;

  /** Score obtained when an adjacent piece to the king is attacked. */
  private static final float ADJACENT_ATTACK_SCORE = 10f;

  /** Score obtained when the king has no adjacent pieces attacked. */
  private static final float NO_ADJACENT_ATTACK_BONUS = 20f;

  /** Score obtained when the king is in the center. */
  private static final float IN_CENTER_SCORE = 20f;

  /** The multiplier used to keep the values under SCORE_CAP. */
  private static final float MULTIPLER =
      SCORE_CAP
          / (8 * PROTECTION_BONUS
              + 8 * ADJACENT_ATTACK_SCORE
              + NO_ADJACENT_ATTACK_BONUS
              + IN_CENTER_SCORE);

  /**
   * Assigns a score to a player according to the safety of his king. Checks: if king is in the
   * center (so more vulnerable), if king has pieces around him to protect him, and there are many
   * checks possible from the enemy onto him.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return score according to the safety of the king
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    score += kingVulnerabilityScore(board, true) - kingVulnerabilityScore(board, false);
    score += kingProtectionScore(board, true) - kingProtectionScore(board, false);
    score += kingSafetyToChecksFromEnemy(board, true) - kingSafetyToChecksFromEnemy(board, false);

    score *= MULTIPLER;

    return isWhite ? score : -score;
  }

  /**
   * Penalizes (or not) the king for being in the center (as it makes him more vulnerable).
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a penalty score (negative) if the king is in the center, 0 otherwise
   */
  private float kingVulnerabilityScore(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;

    // Define center area
    final Position posTopLeftCenter = new Position(2, 5);
    final Position posTopRightCenter = new Position(5, 5);
    final Position posDownLeftCenter = new Position(2, 2);
    final Position posDownRightCenter = new Position(5, 2);

    final Position kingPosition = board.getKing(isWhite).get(0);

    final boolean isKingInCenter =
        kingPosition.x() >= posDownLeftCenter.x()
            && kingPosition.x() <= posTopRightCenter.x()
            && kingPosition.y() >= posDownRightCenter.y()
            && kingPosition.y() <= posTopLeftCenter.y();

    if (isKingInCenter) {
      // Penalize king in the center
      score = -IN_CENTER_SCORE;
    }

    return score;
  }

  /**
   * Assesses how well the king is protected by friendly pieces.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a positive score if the king has friendly pieces nearby, 0 otherwise
   */
  private float kingProtectionScore(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;

    final Position kingPos = board.getKing(isWhite).get(0);

    // Squares around the king
    final int[][] directions = {
      {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, /*King pos*/ {0, 1}, {1, -1}, {1, 0}, {1, 1}
    };
    Position newPos;
    for (final int[] dir : directions) {
      final int newX = kingPos.x() + dir[0];
      final int newY = kingPos.y() + dir[1];

      newPos = new Position(newX, newY);

      if (newPos.isValid()) {
        final ColoredPiece piece = board.getPieceAt(newX, newY);
        if (piece.getPiece() != Piece.EMPTY) {
          final Color colorPiece = piece.getColor();
          final boolean white = colorPiece == Color.WHITE;
          if (white == isWhite) {
            // Protection from piece of the same color
            score += PROTECTION_BONUS;
          }
        }
      } else {
        score += PROTECTION_BONUS;
      }
    }

    return score;
  }

  /**
   * Returns a positive or negative score according to the number of neighbors attacked by the
   * enemy.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a negative score if the king's neighbors can get attacked, positive otherwise
   */
  private float kingSafetyToChecksFromEnemy(
      final BoardRepresentation board, final boolean isWhite) {
    float score = 0;

    Position newPos;
    if (isWhite) {
      final Position king = board.getKing(isWhite).get(0);
      for (int i = king.x() - 1; i < king.x() + 2; i++) {
        for (int j = king.y() - 1; j < king.y() + 2; j++) {
          newPos = new Position(i, j);
          if (newPos.isValid()) {
            if (board.isAttacked(i, j, Color.BLACK)) {
              score -= ADJACENT_ATTACK_SCORE;
            }
          }
        }
      }
    }
    if (!isWhite) {
      final Position king = board.getKing(!isWhite).get(0);
      for (int i = king.x() - 1; i < king.x() + 2; i++) {
        for (int j = king.y() - 1; j < king.y() + 2; j++) {
          newPos = new Position(i, j);
          if (newPos.isValid()) {
            if (board.isAttacked(i, j, Color.WHITE)) {
              score -= ADJACENT_ATTACK_SCORE;
            }
          }
        }
      }
    }

    // No checks available from enemy so good score
    if (score == 0) {
      score += NO_ADJACENT_ATTACK_BONUS;
    }

    return score;
  }
}
