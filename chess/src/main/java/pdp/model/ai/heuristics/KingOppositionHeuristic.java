package pdp.model.ai.heuristics;

import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

public class KingOppositionHeuristic implements Heuristic {

  /**
   * Computes a score according to the (un)balance of the kings position. The more the kings are in
   * opposition, the more likely it is for the endgame to be drawish. Heuristic used for endgames.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score depending on the progress of the pawns
   */
  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += evaluateKingOpposition(board, isWhite);
    return score;
  }

  @Override
  public boolean isThreefoldImpact() {
    return false;
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

    int diffX = Math.abs(whiteKing.getX() - blackKing.getX());
    int diffY = Math.abs(whiteKing.getY() - blackKing.getY());

    // If kings are directly opposite with one square between them
    if ((diffX == 2 && diffY == 0) || (diffY == 2 && diffX == 0)) {
      // Strong opposition so more drawish
      return -10;
    }
    // If kings are diagonally close
    if (diffX <= 2 && diffY <= 2) {
      // Marginally drawish cuz slight opposition
      return -5;
    }
    // No real opposition
    return 0;
  }
}
