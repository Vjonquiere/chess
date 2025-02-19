package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.utils.Position;

public class DevelopmentHeuristic implements Heuristic {

  /**
   * Computes and returns a score corresponding to the level of development for each player
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on the development of each player
   */
  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score =
        evaluatePiecesDevelopment(board, isWhite) - evaluatePiecesDevelopment(board, !isWhite);
    return score;
  }

  /**
   * Counts the number of pieces that are placed on their home squares If pieces other than pawns
   * and kings are developped then a good score is assigned to the player
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score based on the development of each player
   */
  private int evaluatePiecesDevelopment(Board board, boolean isWhite) {
    int score = 0;
    int bonusForEveryDevelopedPiece = 3;
    int bonusForEveryDevelopedPawn = 1;

    List<List<Position>> initPlayerPos;
    List<List<Position>> currentPlayerPos;

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
      List<Position> currentPositions = currentPlayerPos.get(i);
      List<Position> initialPositions = initPlayerPos.get(i);
      for (Position pos : currentPositions) {
        // If the piece is not on one of its initial squares, it is considered developed
        if (!initialPositions.contains(pos)) {
          // Pawns for index 5
          if (i == 5) {
            score += bonusForEveryDevelopedPawn;
          } else if (i != 0) {
            // King for index 0
            score += bonusForEveryDevelopedPiece;
          }
        }
      }
    }

    return score;
  }
}
