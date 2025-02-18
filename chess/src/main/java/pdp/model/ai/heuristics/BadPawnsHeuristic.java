package pdp.model.ai.heuristics;

import java.util.*;
import pdp.model.board.Board;
import pdp.utils.Position;

public class BadPawnsHeuristic implements Heuristic {

  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += doubledPawns(board, isWhite) - doubledPawns(board, !isWhite);
    score += isolatedPawns(board, isWhite) - isolatedPawns(board, !isWhite);
    score += backwardsPawns(board, isWhite) - backwardsPawns(board, !isWhite);
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
   * Counts the number of backward pawns (pawns that have no friendly pawns behind them in adjacent
   * columns and are behind enemy pawns) and returns a score accordingly
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return score based on the number of backward pawns
   */
  private int backwardsPawns(Board board, boolean isWhite) {
    return 0;
  }
}
