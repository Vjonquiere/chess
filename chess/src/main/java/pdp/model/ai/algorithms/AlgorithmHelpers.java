package pdp.model.ai.algorithms;

import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;

public abstract class AlgorithmHelpers {

  public static Move promoteMove(Move move) {

    if (move instanceof PromoteMove) {
      return move;
    }

    ColoredPiece piece = move.getPiece();
    if (piece.getPiece() == Piece.PAWN
        && piece.getColor() == Color.BLACK
        && move.getDest().getY() == 0) {
      move = new PromoteMove(move.getSource(), move.getDest(), Piece.QUEEN);
    }
    if (piece.getPiece() == Piece.PAWN
        && piece.getColor() == Color.WHITE
        && move.getDest().getY() == 7) {
      move = new PromoteMove(move.getSource(), move.getDest(), Piece.QUEEN);
    }
    return move;
  }
}
