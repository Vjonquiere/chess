package pdp.model.ai.heuristics;

import pdp.exceptions.InvalidBoardException;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.BoardRepresentation;

/** Heuristic based on the number of pieces still on the board. */
public class MaterialHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  private static final float PAWN_VALUE = 1f;
  private static final float QUEEN_VALUE = 9f;
  private static final float BISHOP_VALUE = 3f;
  private static final float KNIGHT_VALUE = 3f;
  private static final float ROOK_VALUE = 5f;

  /** The multiplier used to keep the values under SCORE_CAP. */
  private static final float MULTIPLIER =
      SCORE_CAP / (QUEEN_VALUE * 9 + BISHOP_VALUE * 2 + KNIGHT_VALUE * 2 + ROOK_VALUE * 2);

  /**
   * Evaluates the board based on the number of pieces still on the board and returns a score.
   *
   * @param board Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    if (!(board instanceof BitboardRepresentation bitboardRep)) {
      throw new InvalidBoardException();
    }
    float score = 0;
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
