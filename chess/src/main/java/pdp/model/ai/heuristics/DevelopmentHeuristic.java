package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

/** Heuristic based on the development (advancement) of pieces. */
public class DevelopmentHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  private static final float BONUS_DEV_PAWN = 1f;
  private static final float BONUS_DEV_PIECE = 3f;

  /** The multiplier used to keep the values under SCORE_CAP. */
  private static final float MULTIPLIER =
      SCORE_CAP / (15 * BONUS_DEV_PIECE); // all pawns have promoted

  /**
   * Computes and returns a score corresponding to the level of development for each player.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on the development of each player
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    final float score =
        evaluatePiecesDevelopment(board, true) - evaluatePiecesDevelopment(board, false);
    return isWhite ? score : -score;
  }

  /**
   * Counts the number of pieces that are placed on their home squares. If pieces other than pawns
   * and kings are developed then a good score is assigned to the player.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on the development of each player
   */
  private float evaluatePiecesDevelopment(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;

    final List<List<Position>> initPlayerPos;
    final List<List<Position>> currentPlayerPos;

    if (isWhite) {
      initPlayerPos = board.getBoardRep().retrieveInitialWhitePiecesPos();
      currentPlayerPos = board.getBoardRep().retrieveWhitePiecesPos();
    } else {
      initPlayerPos = board.getBoardRep().retrieveInitialBlackPiecesPos();
      currentPlayerPos = board.getBoardRep().retrieveBlackPiecesPos();
    }
    // Compare each piece's position to home square position
    // If not on home square, then it is a developed piece
    for (int i = 0; i < currentPlayerPos.size(); i++) {
      final List<Position> currentPositions = currentPlayerPos.get(i);
      final List<Position> initialPositions = initPlayerPos.get(i);
      for (final Position pos : currentPositions) {
        // If the piece is not on one of its initial squares, it is considered developed
        if (!initialPositions.contains(pos)) {
          // Pawns for index 5
          if (i == 5) {
            score += BONUS_DEV_PAWN;
          } else if (i != 0) {
            // King for index 0
            score += BONUS_DEV_PIECE;
          }
        }
      }
    }

    score *= MULTIPLIER;

    return score;
  }
}
