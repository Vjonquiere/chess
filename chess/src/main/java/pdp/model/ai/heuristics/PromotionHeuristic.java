package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

public class PromotionHeuristic implements Heuristic {

  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += pawnsHaveProgressedScore(board, isWhite);
    score += pawnsAreCloseToPromotion(board, isWhite);

    return score;
  }

  /**
   * Checks if the pawns are well advanced for the corresponding color and returns a score
   * accordingly
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score if the pawns were pushed far enough for the majority of them
   */
  public int pawnsHaveProgressedScore(Board board, boolean isWhite) {
    int score = 0;
    if (board.getBoardRep().pawnsHaveProgressed(isWhite)) {
      score += 10;
    }

    return score;
  }

  /**
   * Evaluates how close pawns are to promotion.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on how many pawns are close to promoting.
   */
  public int pawnsAreCloseToPromotion(Board board, boolean isWhite) {
    int score = 0;

    BoardRepresentation bitboard = board.getBoardRep();
    List<Position> pawns = bitboard.getPawns(isWhite);

    final int SECOND_LAST_RANK = isWhite ? 6 : 1;

    for (Position pawn : pawns) {
      if (pawn.getY() == SECOND_LAST_RANK) {
        score += 20; // Pawn one step from promotion
      } else if ((isWhite && pawn.getY() >= 5) || (!isWhite && pawn.getY() <= 2)) {
        score += 10; // Pawn in the final phase of advancement
      }
    }

    return score;
  }
}
