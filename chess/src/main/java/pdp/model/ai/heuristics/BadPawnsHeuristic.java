package pdp.model.ai.heuristics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

/**
 * Heuristic based on the weakness of pawn structure. Takes into account the backward pawns, the
 * isolated pawns and doubled pawns.
 */
public class BadPawnsHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  /** Penalty for the backward pawns. */
  private static final float PENALTY_FOR_BACKWARDS_PAWN = -4f;

  /** Penalty for the isolated pawns. */
  private static final float PENALTY_FOR_ISOLATED_PAWN = -1f;

  /** Penalty for the doubled pawns. */
  private static final float PENALTY_FOR_DOUBLED_PAWN = -1f;

  private static final float MULTIPLIER =
      SCORE_CAP
          / Math.abs(
              PENALTY_FOR_BACKWARDS_PAWN * 4
                  + PENALTY_FOR_DOUBLED_PAWN * 4
                  + PENALTY_FOR_ISOLATED_PAWN * 8);

  /**
   * Computes a score according to the potential weaknesses in the observed pawn structures.
   *
   * @param board hte board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on how bad pawns are for the corresponding player
   */
  @Override
  public float evaluate(final Board board, final boolean isWhite) {
    float score = 0;
    score += (doubledPawns(board, true) - doubledPawns(board, false)) * PENALTY_FOR_DOUBLED_PAWN;
    score += (isolatedPawns(board, true) - isolatedPawns(board, false)) * PENALTY_FOR_ISOLATED_PAWN;
    score +=
        (backwardsPawns(board, true) - backwardsPawns(board, false)) * PENALTY_FOR_BACKWARDS_PAWN;

    score *= MULTIPLIER;
    return isWhite ? score : -score;
  }

  /**
   * Counts the doubled pawns ( 2 or more pawns on the same column).
   *
   * @param board Current board
   * @param isWhite true if the player is White, false if he is black
   * @return number of doubled pawns
   */
  private int doubledPawns(final Board board, final boolean isWhite) {
    final Map<Integer, Integer> colCount = new HashMap<>();
    int count = 0;
    for (final Position p : board.getBoardRep().getPawns(isWhite)) {
      colCount.put(p.x(), colCount.getOrDefault(p.x(), 0) + 1);
    }
    for (final int c : colCount.keySet()) {
      if (colCount.get(c) > 1) {
        count++;
      }
    }
    return count;
  }

  /**
   * Counts the number of isolated pawns ( no other friend pawn in the next columns).
   *
   * @param board Current board
   * @param isWhite true if the player is White, false if he is black
   * @return number of isolated pawns
   */
  private int isolatedPawns(final Board board, final boolean isWhite) {
    final Set<Integer> occupiedFiles = new HashSet<>();
    final List<Position> pawns = board.getBoardRep().getPawns(isWhite);
    for (final Position p : pawns) {
      occupiedFiles.add(p.x());
    }
    int count = 0;
    for (final Position pos : pawns) {
      final int file = pos.x();
      final boolean hasLeft = occupiedFiles.contains(file - 1);
      final boolean hasRight = occupiedFiles.contains(file + 1);

      if (!hasLeft && !hasRight) {
        count++;
      }
    }
    return count;
  }

  /**
   * Counts the number of backward pawns and computes the corresponding score. Essentially checks:
   * If it has no friendly pawns behind it in adjacent files. If it is the most backward pawn in its
   * column. If an enemy pawn is ahead of it in the same file. If all conditions hold, it is a
   * backward pawn.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return score based on the number of backward pawns
   */
  private int backwardsPawns(final Board board, final boolean isWhite) {
    final BoardRepresentation bitboard = board.getBoardRep();
    final List<Position> pawns = bitboard.getPawns(isWhite);
    final List<Position> enemyPawns = bitboard.getPawns(!isWhite);

    int count = 0;

    // Track the most advanced friendly pawn in each file
    final Map<Integer, Integer> highestPawnRank = new HashMap<>();
    for (final Position p : pawns) {
      final int currentY = p.y();
      if (isWhite) {
        highestPawnRank.put(p.x(), Math.max(highestPawnRank.getOrDefault(p.x(), 0), currentY));
      } else {
        highestPawnRank.put(p.x(), Math.min(highestPawnRank.getOrDefault(p.x(), 7), currentY));
      }
    }

    // Track the closest enemy pawn in each file// Should be equal
    final Map<Integer, Integer> closestEnemyRank = new HashMap<>();
    for (final Position p : enemyPawns) {
      final int currentY = p.y();
      if (isWhite) {
        closestEnemyRank.put(p.x(), Math.min(closestEnemyRank.getOrDefault(p.x(), 8), currentY));
      } else {
        closestEnemyRank.put(p.x(), Math.max(closestEnemyRank.getOrDefault(p.x(), -1), currentY));
      }
    }

    for (final Position pawn : pawns) {
      final int x = pawn.x();
      final int y = pawn.y();

      // Check if this is the most backward pawn in its file
      final boolean isMostBackwards;
      // Check if there are enemy pawns ahead in the same file
      final boolean enemyIsAhead;
      // Check potential pawn support from left and from right
      final boolean hasLeftSupport;
      final boolean hasRightSupport;

      if (isWhite) {
        hasLeftSupport = highestPawnRank.getOrDefault(x - 1, -1) <= y;
        hasRightSupport = highestPawnRank.getOrDefault(x + 1, -1) <= y;
        isMostBackwards = highestPawnRank.getOrDefault(x, -1) == y;
        enemyIsAhead = closestEnemyRank.getOrDefault(x, 8) > y;
      } else {
        hasLeftSupport = highestPawnRank.getOrDefault(x - 1, 8) >= y;
        hasRightSupport = highestPawnRank.getOrDefault(x + 1, 8) >= y;
        isMostBackwards = highestPawnRank.getOrDefault(x, 8) == y;
        enemyIsAhead = closestEnemyRank.getOrDefault(x, -1) < y;
      }

      // If the pawn has no support, is the most backward in its file, and is behind enemy pawns
      if (!hasLeftSupport && !hasRightSupport && isMostBackwards && enemyIsAhead) {
        count++;
      }
    }

    return count;
  }
}
