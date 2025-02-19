package pdp.model.ai.heuristics;

import java.util.*;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

public class BadPawnsHeuristic implements Heuristic {

  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += doubledPawns(board, isWhite) - doubledPawns(board, !isWhite);
    score += isolatedPawns(board, isWhite) - isolatedPawns(board, !isWhite);
    // score += backwardsPawns(board, isWhite) - backwardsPawns(board, !isWhite);
    return (int) (-0.5 * score);
  }

  /**
   * Counts the doubled pawns ( 2 or more pawns on the same column)
   *
   * @param board Current board
   * @param isWhite true if the player is White, false if he is black
   * @return number of doubled pawns
   */
  private int doubledPawns(Board board, boolean isWhite) {
    Map<Integer, Integer> colCount = new HashMap<>();
    int count = 0;
    for (Position p : board.getBoardRep().getPawns(isWhite)) {
      colCount.put(p.getX(), colCount.getOrDefault(p.getX(), 0) + 1);
    }
    for (int c : colCount.keySet()) {
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
  private int isolatedPawns(Board board, boolean isWhite) {
    Set<Integer> occupiedFiles = new HashSet<>();
    List<Position> pawns = board.getBoardRep().getPawns(isWhite);
    for (Position p : pawns) {
      occupiedFiles.add(p.getX());
    }
    int count = 0;
    for (Position pos : pawns) {
      int file = pos.getX();
      boolean hasLeft = occupiedFiles.contains(file - 1);
      boolean hasRight = occupiedFiles.contains(file + 1);

      if (!hasLeft && !hasRight) {
        count++;
      }
    }
    return count;
  }

  /**
   * Counts the number of backward pawns a computes the corresponding score. Essentially checks: If
   * it has no friendly pawns behind it in adjacent files. If it is the most backward pawn in its
   * column. If an enemy pawn is ahead of it in the same file. If all conditions hold, it is a
   * backward pawn.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return score based on the number of backward pawns
   */
  private int backwardsPawns(Board board, boolean isWhite) {
    BoardRepresentation bitboard = board.getBoardRep();
    List<Position> pawns = bitboard.getPawns(isWhite);
    List<Position> enemyPawns = bitboard.getPawns(!isWhite);

    int count = 0;
    int penaltyForBackWardsPawn = 4;

    // Store highest rank for each file where a friendly pawn exists
    Map<Integer, Integer> highestPawnRank = new HashMap<>();
    for (Position p : pawns) {
      highestPawnRank.put(p.getX(), Math.max(highestPawnRank.getOrDefault(p.getX(), 0), p.getY()));
    }

    // Store lowest enemy pawn rank for each file
    Map<Integer, Integer> lowestEnemyRank = new HashMap<>();
    for (Position p : enemyPawns) {
      lowestEnemyRank.put(p.getX(), Math.min(lowestEnemyRank.getOrDefault(p.getX(), 7), p.getY()));
    }

    for (Position pawn : pawns) {
      int x = pawn.getX();
      int y = pawn.getY();

      // Has a potential support from friendly pawn on the left
      boolean hasLeftSupport = highestPawnRank.getOrDefault(x - 1, -1) >= y;
      // Has a potential support from friendly pawn on the right
      boolean hasRightSupport = highestPawnRank.getOrDefault(x + 1, -1) >= y;
      // Is the most backwards pawn in the pawn chain so weaker pawn
      boolean isMostBackwards = highestPawnRank.getOrDefault(x, -1) == y;
      // Not a passed pawn so weaker pawn
      boolean enemyIsAhead = lowestEnemyRank.getOrDefault(x, 8) < y;

      if (!hasLeftSupport && !hasRightSupport && isMostBackwards && enemyIsAhead) {
        count++;
      }
    }

    return count * penaltyForBackWardsPawn;
  }
}
