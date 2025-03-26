package pdp.model.ai.heuristics;

import pdp.model.board.Board;
import pdp.model.piece.Color;

/** Heuristic adding/ removing points in case of checkmate. */
public class GameStatus implements Heuristic {

  /**
   * Evaluates the board based on the possible checkmates.
   *
   * @param board Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public float evaluate(Board board, boolean isWhite) {
    int score = 0;
    if (board.getBoardRep().isCheckMate(Color.WHITE)) {
      score -= 10000;
    }
    if (board.getBoardRep().isCheckMate(Color.BLACK)) {
      score += 10000;
    }
    return isWhite ? score : -score;
  }
}
