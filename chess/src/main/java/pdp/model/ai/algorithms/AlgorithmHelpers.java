package pdp.model.ai.algorithms;

import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;

/** Utility class to add promotion moves to the AI algorithms. */
public final class AlgorithmHelpers {

  /** Private constructor to prevent instantiation. */
  private AlgorithmHelpers() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
  }

  /**
   * Promotes a pawn move if it reaches the last rank or retrieves the original move otherwise.
   *
   * @param move The move to be checked for promotion.
   * @return A promoted move or the original if it is not a promotion.
   */
  public static Move promoteMove(Move move) {

    if (move instanceof PromoteMove) {
      return move;
    }

    Move newMove = move;
    final ColoredPiece piece = move.getPiece();
    if (piece.getPiece() == Piece.PAWN
        && piece.getColor() == Color.BLACK
        && move.getDest().y() == 0) {
      newMove = new PromoteMove(move.getSource(), move.getDest(), Piece.QUEEN);
    }
    if (piece.getPiece() == Piece.PAWN
        && piece.getColor() == Color.WHITE
        && move.getDest().y() == 7) {
      newMove = new PromoteMove(move.getSource(), move.getDest(), Piece.QUEEN);
    }
    return newMove;
  }
}
