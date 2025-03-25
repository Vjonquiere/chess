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

    final ColoredPiece piece = move.getPiece();
    if (piece.piece == Piece.PAWN && piece.color == Color.BLACK && move.dest.y() == 0) {
      move = new PromoteMove(move.source, move.dest, Piece.QUEEN);
    }
    if (piece.piece == Piece.PAWN && piece.color == Color.WHITE && move.dest.y() == 7) {
      move = new PromoteMove(move.source, move.dest, Piece.QUEEN);
    }
    return move;
  }
}
