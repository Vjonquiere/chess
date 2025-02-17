package pdp.model.ai.heuristics;

import pdp.model.Game;
import pdp.model.board.BitboardRepresentation;

public class MaterialHeuristic implements Heuristic {

  /**
   * Evaluates the board based on the number of pieces still on the board and returns a score.
   *
   * @param game Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public int evaluate(Game game, boolean isWhite) {
    int score = 0;
    if (!(game.getBoard().getBoardRep() instanceof BitboardRepresentation bitboardRepresentation))
      throw new RuntimeException("Only available for bitboards");
    score +=
        bitboardRepresentation.getPawns(isWhite).size()
            - bitboardRepresentation.getPawns(!isWhite).size();
    score +=
        (bitboardRepresentation.getQueens(isWhite).size()
                - bitboardRepresentation.getQueens(!isWhite).size())
            * 9;
    score +=
        (bitboardRepresentation.getBishops(isWhite).size()
                - bitboardRepresentation.getBishops(!isWhite).size())
            * 3;
    score +=
        (bitboardRepresentation.getKnights(isWhite).size()
                - bitboardRepresentation.getKnights(!isWhite).size())
            * 3;
    score +=
        (bitboardRepresentation.getRooks(isWhite).size()
                - bitboardRepresentation.getRooks(!isWhite).size())
            * 5;
    return score;
  }
}
