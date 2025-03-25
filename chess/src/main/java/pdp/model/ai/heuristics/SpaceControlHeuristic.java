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
  public float evaluate(Board board, boolean isWhite) {
    int score = 0;
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
  private int evaluateCenterControl(Board board, boolean isWhite) {
    int score = 0;
    int bonusForEachMoveInCenter = 3;

    List<Move> allPossibleMoves = board.getBoardRep().getAllAvailableMoves(isWhite);

    // Delineate center box
    Position posTopLeftCenter = new Position(2, 5);
    Position posTopRightCenter = new Position(5, 5);
    Position posDownLeftCenter = new Position(2, 2);
    Position posDownRightCenter = new Position(5, 2);

    for (Move move : allPossibleMoves) {
      Position posDest = move.getDest();
      // If move is aiming at the center then bonus
      boolean moveAimsAtCenter =
          posDest.x() >= posDownLeftCenter.x()
              && posDest.x() <= posTopRightCenter.x()
              && posDest.y() >= posDownRightCenter.y()
              && posDest.y() <= posTopLeftCenter.y();
      if (moveAimsAtCenter) {
        score += bonusForEachMoveInCenter;
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
  private int evaluateFlanksControl(Board board, boolean isWhite) {
    int score = 0;
    int bonusForEachMoveOnFlanks = 1;

    List<Move> allPossibleMoves = board.getBoardRep().getAllAvailableMoves(isWhite);

    // Left flank
    Position posTopLeftCenterLeftFlank = new Position(0, 5);
    Position posTopRightCenterLeftFlank = new Position(1, 5);
    Position posDownLeftCenterLeftFlank = new Position(0, 2);
    Position posDownRightCenterLeftFlank = new Position(1, 2);

    // Right flank
    Position posTopLeftCenterRightFlank = new Position(5, 5);
    Position posTopRightCenterRightFlank = new Position(6, 5);
    Position posDownLeftCenterRightFlank = new Position(5, 2);
    Position posDownRightCenterRightFlank = new Position(6, 2);

    for (Move move : allPossibleMoves) {
      Position posDest = move.getDest();
      // If move is aiming at the left flank then bonus
      boolean moveAimsAtLeftFlank =
          posDest.x() >= posDownLeftCenterLeftFlank.x()
              && posDest.x() <= posTopRightCenterLeftFlank.x()
              && posDest.y() >= posDownRightCenterLeftFlank.y()
              && posDest.y() <= posTopLeftCenterLeftFlank.y();
      if (moveAimsAtLeftFlank) {
        score += bonusForEachMoveOnFlanks;
      }
      // If move is aiming at the right flank then bonus
      boolean moveAimsAtRightFlank =
          posDest.x() >= posDownLeftCenterRightFlank.x()
              && posDest.x() <= posTopRightCenterRightFlank.x()
              && posDest.y() >= posDownRightCenterRightFlank.y()
              && posDest.y() <= posTopLeftCenterRightFlank.y();
      if (moveAimsAtRightFlank) {
        score += bonusForEachMoveOnFlanks;
      }
    }

    return score;
  }
}
