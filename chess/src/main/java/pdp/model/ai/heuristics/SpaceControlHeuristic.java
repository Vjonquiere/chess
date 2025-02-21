package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.utils.Position;

public class SpaceControlHeuristic implements Heuristic {

  /**
   * Gives a score based on how much control over the entire board the players have. Center is
   * generally more important so it has more impact on the score
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score corresponding to the overall control of the board
   */
  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += evaluateCenterControl(board, isWhite) - evaluateCenterControl(board, !isWhite);
    score += evaluateFlanksControl(board, isWhite) - evaluateFlanksControl(board, !isWhite);
    return score;
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
          posDest.getX() >= posDownLeftCenter.getX()
              && posDest.getX() <= posTopRightCenter.getX()
              && posDest.getY() >= posDownRightCenter.getY()
              && posDest.getY() <= posTopLeftCenter.getY();
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
          posDest.getX() >= posDownLeftCenterLeftFlank.getX()
              && posDest.getX() <= posTopRightCenterLeftFlank.getX()
              && posDest.getY() >= posDownRightCenterLeftFlank.getY()
              && posDest.getY() <= posTopLeftCenterLeftFlank.getY();
      if (moveAimsAtLeftFlank) {
        score += bonusForEachMoveOnFlanks;
      }
      // If move is aiming at the right flank then bonus
      boolean moveAimsAtRightFlank =
          posDest.getX() >= posDownLeftCenterRightFlank.getX()
              && posDest.getX() <= posTopRightCenterRightFlank.getX()
              && posDest.getY() >= posDownRightCenterRightFlank.getY()
              && posDest.getY() <= posTopLeftCenterRightFlank.getY();
      if (moveAimsAtRightFlank) {
        score += bonusForEachMoveOnFlanks;
      }
    }

    return score;
  }
}
