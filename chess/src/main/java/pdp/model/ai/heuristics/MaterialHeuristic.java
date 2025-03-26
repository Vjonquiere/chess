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
  public float evaluate(Board board, boolean isWhite) {
    int score = 0;
    if (!(board.getBoardRep() instanceof BitboardRepresentation bitboardRepresentation)) {
      throw new InvalidBoardException();
    }
    score +=
        bitboardRepresentation.getPawns(true).size()
            - bitboardRepresentation.getPawns(false).size();
    score +=
        (bitboardRepresentation.getQueens(true).size()
                - bitboardRepresentation.getQueens(false).size())
            * 9;
    score +=
        (bitboardRepresentation.getBishops(true).size()
                - bitboardRepresentation.getBishops(false).size())
            * 3;
    score +=
        (bitboardRepresentation.getKnights(true).size()
                - bitboardRepresentation.getKnights(false).size())
            * 3;
    score +=
        (bitboardRepresentation.getRooks(true).size()
                - bitboardRepresentation.getRooks(false).size())
            * 5;
    return isWhite ? score : -score;
  }
}
