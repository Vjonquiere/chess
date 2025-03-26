package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

/** Heuristic based on the activity of the king. */
public class KingActivityHeuristic implements Heuristic {

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

    return isWhite ? score : -score;
  }

  /**
   * Checks the location of the king and returns a score accordingly.
   *
   * @param board board of the game
   * @param isWhite true if this is for white, false otherwise
   * @return score according to the location of the king on the board
   */
  private int kingIsInCenterScore(final Board board, final boolean isWhite) {
    int score;

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
      score = 20;
    } else {
      // Compute Manhattan distance to center
      final int centerX = (posTopLeftCenter.x() + posTopRightCenter.x()) / 2;
      final int centerY = (posDownLeftCenter.y() + posTopLeftCenter.y()) / 2;

      final int distance =
          Math.abs(kingPosition.x() - centerX) + Math.abs(kingPosition.y() - centerY);
      final int noBonus = 0;
      // King more or less far from the center
      score = Math.max(noBonus, 15 - (distance * 3));
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
  private int kingActivityScore(final Board board, final boolean isWhite) {
    int score = 0;
    final BoardRepresentation bitboard = board.getBoardRep();
    // Check the activity of the King
    final List<Move> kingMoves = bitboard.retrieveKingMoves(isWhite);
    if (kingMoves.size() >= 5) {
      score += 10;
    }

    return score;
  }
}
