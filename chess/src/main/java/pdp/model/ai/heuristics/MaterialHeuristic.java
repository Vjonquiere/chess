package pdp.model.ai.heuristics;

import pdp.exceptions.InvalidBoardException;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Board;

/** Heuristic based on the number of pieces still on the board. */
public class MaterialHeuristic implements Heuristic {

  /**
   * Evaluates the board based on the number of pieces still on the board and returns a score.
   *
   * @param board Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public float evaluate(final Board board, final boolean isWhite) {
    if (!(board.getBoardRep() instanceof BitboardRepresentation bitboardRep)) {
      throw new InvalidBoardException();
    }
    int score = 0;
    score += bitboardRep.getPawns(true).size() - bitboardRep.getPawns(false).size();
    score += (bitboardRep.getQueens(true).size() - bitboardRep.getQueens(false).size()) * 9;
    score += (bitboardRep.getBishops(true).size() - bitboardRep.getBishops(false).size()) * 3;
    score += (bitboardRep.getKnights(true).size() - bitboardRep.getKnights(false).size()) * 3;
    score += (bitboardRep.getRooks(true).size() - bitboardRep.getRooks(false).size()) * 5;
    return isWhite ? score : -score;
  }
}
