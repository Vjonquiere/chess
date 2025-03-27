package pdp.model.ai.heuristics;

import pdp.exceptions.InvalidBoardException;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Board;

/** Heuristic based on the number of pieces still on the board. */
public class MaterialHeuristic implements Heuristic {

  private static final int SCORE_CAP = 100;
  private static final int PAWN_VALUE = 1;
  private static final int QUEEN_VALUE = 9;
  private static final int BISHOP_VALUE = 3;
  private static final int KNIGHT_VALUE = 3;
  private static final int ROOK_VALUE = 5;
  private static final float MULTIPLIER =
      SCORE_CAP / (QUEEN_VALUE * 8 + BISHOP_VALUE * 2 + KNIGHT_VALUE * 2 + ROOK_VALUE * 2);

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
    score += (bitboardRep.getPawns(true).size() - bitboardRep.getPawns(false).size()) * PAWN_VALUE;
    score +=
        (bitboardRep.getQueens(true).size() - bitboardRep.getQueens(false).size()) * QUEEN_VALUE;
    score +=
        (bitboardRep.getBishops(true).size() - bitboardRep.getBishops(false).size()) * BISHOP_VALUE;
    score +=
        (bitboardRep.getKnights(true).size() - bitboardRep.getKnights(false).size()) * KNIGHT_VALUE;
    score += (bitboardRep.getRooks(true).size() - bitboardRep.getRooks(false).size()) * ROOK_VALUE;

    score *= MULTIPLIER;

    return isWhite ? score : -score;
  }
}
