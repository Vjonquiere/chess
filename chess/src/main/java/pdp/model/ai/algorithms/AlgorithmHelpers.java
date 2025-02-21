package pdp.model.ai.algorithms;

import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;

public abstract class AlgorithmHelpers {

  public static Move promoteMove(Move move) {
    ColoredPiece piece = move.getPiece();
    if (piece.piece == Piece.PAWN && piece.color == Color.BLACK && move.dest.getY() == 0) {
      move = new PromoteMove(move.source, move.dest, Piece.QUEEN);
    }
    if (piece.piece == Piece.PAWN && piece.color == Color.WHITE && move.dest.getY() == 7) {
      move = new PromoteMove(move.source, move.dest, Piece.QUEEN);
    }
    return move;
  }
}
