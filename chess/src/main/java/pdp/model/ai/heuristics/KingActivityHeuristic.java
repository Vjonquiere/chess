package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

public class KingActivityHeuristic implements Heuristic {

  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;

    // Delineate center box
    Position posTopLeftCenter = new Position(2, 5);
    Position posTopRightCenter = new Position(5, 5);
    Position posDownLeftCenter = new Position(2, 2);
    Position posDownRightCenter = new Position(5, 2);

    Position kingPosition = board.getBoardRep().getKing(isWhite).get(0);
    // Check if the king is close to the center of the board and therefore has easier access to the
    // entire board
    boolean isKingInCenter =
        kingPosition.getX() >= posDownLeftCenter.getX()
            && kingPosition.getX() <= posTopRightCenter.getX()
            && kingPosition.getY() >= posDownRightCenter.getY()
            && kingPosition.getY() <= posTopLeftCenter.getY();

    int kingCenterBonus = 0;
    if (isKingInCenter) {
      kingCenterBonus = 20;
    } else {
      // Compute Manhattan distance to center
      int centerX = (posTopLeftCenter.getX() + posTopRightCenter.getX()) / 2;
      int centerY = (posDownLeftCenter.getY() + posTopLeftCenter.getY()) / 2;

      int distance =
          Math.abs(kingPosition.getX() - centerX) + Math.abs(kingPosition.getY() - centerY);
      int noBonus = 0;
      // King more or less far of the center
      kingCenterBonus = Math.max(noBonus, 15 - (distance * 3));
    }

    score += kingCenterBonus;

    BoardRepresentation bitboard = board.getBoardRep();
    // Check the activity of the King
    List<Move> kingMoves = bitboard.retrieveKingMoves(isWhite);
    if (kingMoves.size() >= 5) {
      score += 10;
    }

    return score;
  }
}
