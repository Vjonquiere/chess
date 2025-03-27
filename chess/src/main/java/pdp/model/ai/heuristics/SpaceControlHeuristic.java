package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.utils.Position;

/**
 * Heuristic based on the amount of control of the board the players have. More control in the
 * center is important.
 */
public class SpaceControlHeuristic implements Heuristic {

  /**
   * Gives a score based on how much control over the entire board the players have. Center is
   * generally more important, so it has more impact on the score.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score corresponding to the overall control of the board
   */
  @Override
  public float evaluate(final Board board, final boolean isWhite) {
    float score = 0;
    score += evaluateCenterControl(board, true) - evaluateCenterControl(board, false);
    score += evaluateFlanksControl(board, true) - evaluateFlanksControl(board, false);
    return isWhite ? score : -score;
  }

  /**
   * Gives a score based on how much control over the center the corresponding player has.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on center control
   */
  private float evaluateCenterControl(final Board board, final boolean isWhite) {
    float score = 0;
    final float bonusMoveInCenter = 3f;

    final List<Move> allPossibleMoves = board.getBoardRep().getAllAvailableMoves(isWhite);

    // Delineate center box
    final Position posTopLeftCenter = new Position(2, 5);
    final Position posTopRightCenter = new Position(5, 5);
    final Position posDownLeftCenter = new Position(2, 2);
    final Position posDownRightCenter = new Position(5, 2);

    for (final Move move : allPossibleMoves) {
      final Position posDest = move.getDest();
      // If move is aiming at the center then bonus
      final boolean moveAimsAtCenter =
          posDest.x() >= posDownLeftCenter.x()
              && posDest.x() <= posTopRightCenter.x()
              && posDest.y() >= posDownRightCenter.y()
              && posDest.y() <= posTopLeftCenter.y();
      if (moveAimsAtCenter) {
        score += bonusMoveInCenter;
      }
    }

    return score;
  }

  /**
   * Gives a score based on how much control over the flanks the corresponding player has.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on flanks control
   */
  private float evaluateFlanksControl(final Board board, final boolean isWhite) {
    float score = 0;
    final float bonusMoveOnFlanks = 1f;

    final List<Move> allPossibleMoves = board.getBoardRep().getAllAvailableMoves(isWhite);

    // Left flank
    final Position posTopLeftCenterLeftFlank = new Position(0, 5);
    final Position posTopRightCenterLeftFlank = new Position(1, 5);
    final Position posDownLeftCenterLeftFlank = new Position(0, 2);
    final Position posDownRightCenterLeftFlank = new Position(1, 2);

    // Right flank
    final Position posTopLeftCenterRightFlank = new Position(5, 5);
    final Position posTopRightCenterRightFlank = new Position(6, 5);
    final Position posDownLeftCenterRightFlank = new Position(5, 2);
    final Position posDownRightCenterRightFlank = new Position(6, 2);

    for (final Move move : allPossibleMoves) {
      final Position posDest = move.getDest();
      // If move is aiming at the left flank then bonus
      final boolean moveAimsAtLeftFlank =
          posDest.x() >= posDownLeftCenterLeftFlank.x()
              && posDest.x() <= posTopRightCenterLeftFlank.x()
              && posDest.y() >= posDownRightCenterLeftFlank.y()
              && posDest.y() <= posTopLeftCenterLeftFlank.y();
      if (moveAimsAtLeftFlank) {
        score += bonusMoveOnFlanks;
      }
      // If move is aiming at the right flank then bonus
      final boolean moveAimsAtRightFlank =
          posDest.x() >= posDownLeftCenterRightFlank.x()
              && posDest.x() <= posTopRightCenterRightFlank.x()
              && posDest.y() >= posDownRightCenterRightFlank.y()
              && posDest.y() <= posTopLeftCenterRightFlank.y();
      if (moveAimsAtRightFlank) {
        score += bonusMoveOnFlanks;
      }
    }

    return score;
  }
}
