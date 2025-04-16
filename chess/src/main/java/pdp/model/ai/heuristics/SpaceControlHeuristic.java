package pdp.model.ai.heuristics;

import java.util.ArrayList;
import java.util.List;
import pdp.model.board.*;
import pdp.utils.Position;

/**
 * Heuristic based on the amount of control of the board the players have. More control in the
 * center is important.
 */
public class SpaceControlHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  /** Maximum number of moves considered in the evaluation. */
  private static final int MAX_MOVES_CONSIDERED = 40;

  /** Bonus score for a move in the center of the board. */
  private static final float BONUS_MOVE_IN_CENTER = 3f;

  /** Bonus score for a move on the flanks (edges of the board). */
  private static final float BONUS_MOVE_ON_FLANKS = 1.5f;

  /** Bonus score for a move elsewhere on the board. */
  private static final float BONUS_MOVE_ELSEWHERE = 0.5f;

  /**
   * Maximum number of moves in the center considered to equilibrate the distribution in the
   * multiplier.
   */
  private static final int MAX_MOVES_CENTER = 4;

  /**
   * Maximum number of moves in the flanks considered to equilibrate the distribution in the
   * multiplier.
   */
  private static final int MAX_MOVES_FLANKS = 2 * 8;

  /** The multiplier used to keep the values under SCORE_CAP. */
  private static final float MULTIPLIER =
      SCORE_CAP
          / ((MAX_MOVES_CENTER * BONUS_MOVE_IN_CENTER)
              + (MAX_MOVES_FLANKS * BONUS_MOVE_ON_FLANKS)
              + (MAX_MOVES_CONSIDERED
                  - (MAX_MOVES_CENTER + MAX_MOVES_FLANKS) * BONUS_MOVE_ELSEWHERE));

  /** Minimum x-coordinate for the center region. */
  private static final int CENTER_X_MIN = 3;

  /** Maximum x-coordinate for the center region. */
  private static final int CENTER_X_MAX = 4;

  /** Minimum y-coordinate for the center region. */
  private static final int CENTER_Y_MIN = 3;

  /** Maximum y-coordinate for the center region. */
  private static final int CENTER_Y_MAX = 4;

  /** Maximum x-coordinate for the left flank region. */
  private static final int FLANK_LEFT_MAX_X = 0;

  /** Minimum x-coordinate for the right flank region. */
  private static final int FLANK_RIGHT_MIN_X = 7;

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

    score = score * MULTIPLIER;

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
    int movesInCenter = 0;
    int movesOnFlanks = 0;
    int movesElsewhere = 0;

    if (board instanceof BitboardRepresentation bitboard) {
      final Bitboard moves = bitboard.getColorAttackBitboard(isWhite);
      for (final Integer moveSquare : moves.getSetBits()) {
        final Position posDest = BitboardUtils.squareToPosition(moveSquare);
        if (isInCenter(posDest)) {
          movesInCenter += 1;
        } else if (isInLeftFlank(posDest) || isInRightFlank(posDest)) {
          movesOnFlanks += 1;
        } else {
          movesElsewhere += 1;
        }
      }

      return compileScores(movesInCenter, movesOnFlanks, movesElsewhere);
    }

    final List<Move> allPossibleMoves = board.getAllAvailableMoves(isWhite);

    final List<Position> toEval = new ArrayList<>();
    for (final Move move : allPossibleMoves) {
      final Position posDest = move.getDest();
      if (toEval.contains(posDest)) {
        continue;
      }
      toEval.add(posDest);
    }

    for (final Position posDest : toEval) {
      if (isInCenter(posDest)) {
        movesInCenter += 1;
      } else if (isInLeftFlank(posDest) || isInRightFlank(posDest)) {
        movesOnFlanks += 1;
      } else {
        movesElsewhere += 1;
      }
    }

    return compileScores(movesInCenter, movesOnFlanks, movesElsewhere);
  }

  /**
   * Computes a score based on the location of moves in the board. The moves in the center and in
   * the flanks are more valuable than the other areas.
   *
   * @param movesInCenter the number of moves made in the center of the board
   * @param movesOnFlanks the number of moves made on the flanks of the board
   * @param movesElsewhere the number of moves made in the other areas
   * @return score for the given move distribution
   */
  public float compileScores(
      final int movesInCenter, final int movesOnFlanks, final int movesElsewhere) {
    int movesConsidered = 0;
    float score = 0;

    if (movesConsidered + movesInCenter < MAX_MOVES_CONSIDERED) {
      movesConsidered += movesInCenter;
      score += movesInCenter * BONUS_MOVE_IN_CENTER;
    } else {
      score += (MAX_MOVES_CONSIDERED - movesConsidered) * BONUS_MOVE_IN_CENTER;
      return score;
    }

    if (movesConsidered + movesOnFlanks < MAX_MOVES_CONSIDERED) {
      movesConsidered += movesOnFlanks;
      score += movesOnFlanks * BONUS_MOVE_ON_FLANKS;
    } else {
      score += (MAX_MOVES_CONSIDERED - movesConsidered) * BONUS_MOVE_ON_FLANKS;
      return score;
    }

    if (movesConsidered + movesElsewhere < MAX_MOVES_CONSIDERED) {
      score += movesElsewhere * BONUS_MOVE_ELSEWHERE;
    } else {
      score += (MAX_MOVES_CONSIDERED - movesConsidered) * BONUS_MOVE_ELSEWHERE;
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

  /** Checks if a position is in the left flank (files a-h). */
  private boolean isInLeftFlank(final Position pos) {
    return pos.x() <= FLANK_LEFT_MAX_X;
  }

  /** Checks if a position is in the right flank (files g-h). */
  private boolean isInRightFlank(final Position pos) {
    return pos.x() >= FLANK_RIGHT_MIN_X;
  }
}
