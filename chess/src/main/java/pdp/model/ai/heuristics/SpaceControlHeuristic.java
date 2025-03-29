package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

/**
 * Heuristic based on the amount of control of the board the players have. More control in the
 * center is important.
 */
public class SpaceControlHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  private static final int MAX_MOVES_CONSIDERED = 50;
  private static final float BONUS_MOVE_IN_CENTER = 3f;
  private static final float BONUS_MOVE_ON_FLANKS = 1.5f;
  private static final float BONUS_MOVE_ELSEWHERE = 0.5f;

  /** The multiplier used to keep the values under SCORE_CAP. */
  private static final float MULTIPLIER = SCORE_CAP / (MAX_MOVES_CONSIDERED * BONUS_MOVE_ELSEWHERE);

  private static final int CENTER_X_MIN = 3;
  private static final int CENTER_X_MAX = 4;
  private static final int CENTER_Y_MIN = 3;
  private static final int CENTER_Y_MAX = 4;
  private static final int FLANK_LEFT_MAX_X = 1;
  private static final int FLANK_RIGHT_MIN_X = 6;

  /**
   * Gives a score based on how much control over the entire board the players have. Center is
   * generally more important, so it has more impact on the score.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score corresponding to the overall control of the board
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    score += evaluateBoardControl(board, true) - evaluateBoardControl(board, false);

    score = Math.min(score * MULTIPLIER, SCORE_CAP);

    return isWhite ? score : -score;
  }

  /**
   * Evaluates control of the board, giving different weights to different areas.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on board control
   */
  private float evaluateBoardControl(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    final List<Move> allPossibleMoves = board.getAllAvailableMoves(isWhite);

    for (final Move move : allPossibleMoves) {
      final Position posDest = move.getDest();
      if (isInCenter(posDest)) {
        score += BONUS_MOVE_IN_CENTER;
      } else if (isInLeftFlank(posDest) || isInRightFlank(posDest)) {
        score += BONUS_MOVE_ON_FLANKS;
      } else {
        score += BONUS_MOVE_ELSEWHERE;
      }
    }
    return score;
  }

  /** Checks if a position is in the center region (d4, d5, e4, e5). */
  private boolean isInCenter(final Position pos) {
    return pos.x() >= CENTER_X_MIN
        && pos.x() <= CENTER_X_MAX
        && pos.y() >= CENTER_Y_MIN
        && pos.y() <= CENTER_Y_MAX;
  }

  /** Checks if a position is in the left flank (files a-b). */
  private boolean isInLeftFlank(final Position pos) {
    return pos.x() <= FLANK_LEFT_MAX_X;
  }

  /** Checks if a position is in the right flank (files g-h). */
  private boolean isInRightFlank(final Position pos) {
    return pos.x() >= FLANK_RIGHT_MIN_X;
  }
}
