package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

public class PromotionHeuristic implements Heuristic {
  /**
   * Computes a score according to the closeness of pawns promoting. Heuristic used for endgames.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score depending on the progress of the pawns
   */
  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += pawnsHaveProgressedScore(board, true) - pawnsHaveProgressedScore(board, false);
    score += pawnsAreCloseToPromotion(board, true) - pawnsAreCloseToPromotion(board, false);

    return isWhite ? score : -score;
  }

  /**
   * Checks if the pawns are well advanced for the corresponding color and returns a score
   * accordingly
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score if the pawns were pushed far enough for the majority of them
   */
  private int pawnsHaveProgressedScore(Board board, boolean isWhite) {
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
  private int pawnsAreCloseToPromotion(Board board, boolean isWhite) {
    int score = 0;

    BoardRepresentation bitboard = board.getBoardRep();
    List<Position> pawns = bitboard.getPawns(isWhite);

    final int secondLastRank = isWhite ? 6 : 1;

    for (Position pawn : pawns) {
      if (pawn.getY() == secondLastRank) {
        // Pawn one step from promotion
        score += 20;
      } else if ((isWhite && pawn.getY() >= 5) || (!isWhite && pawn.getY() <= 2)) {
        // Pawn in the final phase of advancement
        score += 10;
      }
    }

    return score;
  }
}
