package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

/** Heuristic based on the performance of bishops during the endgame phase. */
public class BishopEndgameHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  private static final float BAD_BISHOP_SCORE = 5f;
  private static final float SAME_COLOR_BISHOPS_SAME_PLAYER_SCORE = -10f;
  private static final float CENTRALIZATION_SCORE_MAX = 10f;
  private static final float CENTRALIZATION_SCORE_DECREASE_STEP = 2f;
  private static final float MOBILITY_SCORE = 2f;

  // maximum calculated for 2 bishops, 14 moves

  /** The multiplier used to keep the values under SCORE_CAP. */
  private static final float MULTIPLIER =
      SCORE_CAP
          / (2 * 14 * MOBILITY_SCORE
              + 2 * CENTRALIZATION_SCORE_MAX
              + 2 * BAD_BISHOP_SCORE
              + (-SAME_COLOR_BISHOPS_SAME_PLAYER_SCORE));

  /**
   * Computes a score according to how performant bishops are for an endgame position. Heuristic
   * used for endgames.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score depending on the progress of the pawns
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    score += evaluateBishopMobility(board, true) - evaluateBishopMobility(board, false);
    score +=
        evaluateSameColorBishopsSamePlayer(board, true)
            - evaluateSameColorBishopsSamePlayer(board, false);
    score += evaluateCentralization(board, true) - evaluateCentralization(board, false);
    score += evaluateBadBishop(board, true) - evaluateBadBishop(board, false);

    score *= MULTIPLIER;
    return isWhite ? score : -score;
  }

  /**
   * Increase score for bishops that have more activity.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return score based on how many moves bishops can make
   */
  private float evaluateBishopMobility(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    final BoardRepresentation bitboard = board.getBoardRep();
    final List<Move> bishopMoves = bitboard.retrieveBishopMoves(isWhite);

    score += bishopMoves.size() * MOBILITY_SCORE;
    return score;
  }

  /**
   * Penalizes having two bishops on the same color squares (bad in endgames).
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return Negative score if bishops are on the same color
   */
  private float evaluateSameColorBishopsSamePlayer(
      final BoardRepresentation board, final boolean isWhite) {
    final List<Position> bishops = board.getBoardRep().getBishops(isWhite);
    if (bishops.size() < 2) {
      return 0;
    }

    boolean sameColorBishops = false;
    if (bishops.size() > 0) {
      sameColorBishops =
          (bishops.get(0).x() + bishops.get(0).y()) % 2
              == (bishops.get(1).x() + bishops.get(1).y()) % 2;
    }

    if (sameColorBishops) {
      return SAME_COLOR_BISHOPS_SAME_PLAYER_SCORE;
    } else {
      return 0;
    }
  }

  /**
   * Increase score for bishops that are centralized since they control more of the board.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return score for bishops that are close to the center
   */
  private float evaluateCentralization(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    final List<Position> bishops = board.getBoardRep().getBishops(isWhite);
    final int noBonus = 0;

    for (final Position bishop : bishops) {
      final int fromCenter = Math.abs(bishop.x() - 3) + Math.abs(bishop.y() - 3);
      score +=
          Math.max(
              noBonus,
              CENTRALIZATION_SCORE_MAX - (fromCenter * CENTRALIZATION_SCORE_DECREASE_STEP));
    }

    return score;
  }

  /**
   * Penalizes bishops that are stuck behind their own pawns (bad bishops).
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return Negative score if bishop is blocked by pawns
   */
  private float evaluateBadBishop(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    final List<Position> bishops = board.getBoardRep().getBishops(isWhite);
    final List<Position> pawns = board.getBoardRep().getPawns(isWhite);

    for (final Position bishop : bishops) {
      for (final Position pawn : pawns) {
        if ((bishop.x() + bishop.y()) % 2 == (pawn.x() + pawn.y()) % 2) {
          score -= BAD_BISHOP_SCORE;
        }
      }
    }

    return score;
  }
}
