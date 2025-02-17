package pdp.model.ai.heuristics;

import pdp.model.board.Board;
import pdp.model.piece.Color;

public class OpponentCheck implements Heuristic {

  /**
   * Evaluates the board based on the check state of the king. If the opponent king is in check, add
   * a bonus of 50, if it is checkmate, add a bonus of 100. If the player's king is in check or
   * checkmate, we subtract the same values from the evaluation
   *
   * @param board Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    Color player1 = isWhite ? Color.WHITE : Color.BLACK;
    Color player2 = isWhite ? Color.BLACK : Color.WHITE;
    if (board.getBoardRep().isCheck(player2)) {
      score += 50;
    }
    if (board.getBoardRep().isCheck(player1)) {
      score -= 50;
    }
    if (board.getBoardRep().isCheckMate(player2)) {
      score += 100;
    }
    if (board.getBoardRep().isCheckMate(player1)) {
      score -= 100;
    }
    return score;
  }
}
