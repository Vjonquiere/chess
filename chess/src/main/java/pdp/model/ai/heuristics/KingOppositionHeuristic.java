package pdp.model.ai.heuristics;

import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

public class KingOppositionHeuristic implements Heuristic {

  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += evaluateKingOpposition(board, isWhite);
    return score;
  }

  /**
   * Checks king opposition for endgames. If the kings are in opposition (same file, rank, or
   * diagonal with a small distance), the position is more drawish. Else, more imbalance but cannot
   * know who's got the advantage.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on the king opposition
   */
  private int evaluateKingOpposition(Board board, boolean isWhite) {
    BoardRepresentation bitboard = board.getBoardRep();
    Position whiteKing = bitboard.getKing(true).get(0);
    Position blackKing = bitboard.getKing(false).get(0);

    int xDiff = Math.abs(whiteKing.getX() - blackKing.getX());
    int yDiff = Math.abs(whiteKing.getY() - blackKing.getY());

    // If kings are directly opposite with one square between them
    if ((xDiff == 2 && yDiff == 0) || (yDiff == 2 && xDiff == 0)) {
      // Strong opposition so more drawish
      return -10;
    }
    // If kings are diagonally close
    if (xDiff <= 2 && yDiff <= 2) {
      // Marginally drawish cuz slight opposition
      return -5;
    }
    // No real opposition
    return 0;
  }
}
