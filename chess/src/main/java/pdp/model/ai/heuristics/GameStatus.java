package pdp.model.ai.heuristics;

import pdp.model.board.Board;
import pdp.model.piece.Color;

public class GameStatus implements Heuristic {

  /**
   * Evaluates the board based on the possible game status.
   *
   * <ul>
   *   <li>If the opponent king is in check, add a bonus of 500
   *   <li>If the opponent king checkmate add a bonus of 1000.
   *   <li>If our king is in these position, subtract the according amount
   *   <li>If the game is in a Threefold repetition, add a malus of 750
   *   <li>For the fifty move rule, add a malus of 750 (proportional to the percentage of
   *       advencement of the 50 move rule)
   * </ul>
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
    /*
    if (board.getBoardRep().isCheck(player2)) {
      score += 50;
    }
    if (board.getBoardRep().isCheck(player1)) {
      score -= 50;
    }*/
    if (board.getBoardRep().isCheckMate(Color.WHITE)) {
      score -= 10000;
    }
    if (board.getBoardRep().isCheckMate(Color.BLACK)) {
      score += 10000;
    }
    /*
    if (Game.getInstance().getGameState().isThreefoldRepetition()) {
      score -= 750;
    }
    score -= 750 * ((board.getNbMovesWithNoCaptureOrPawn() * 2) / 100);
     */
    return isWhite ? score : -score;
  }
}
