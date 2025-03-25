package pdp.model.savers;

import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.parsers.FenHeader;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;

/** Save board to custom format. */
public class BoardSaver {

  /**
   * Generate the String corresponding to the current player and board. (This String can be directly
   * load by the board parser)
   *
   * @param board The board and current player to save
   * @return The board and current player String
   */
  public static String saveBoard(FileBoard board) {
    StringBuilder sb = new StringBuilder();
    // Save current player
    if (board.isWhiteTurn()) {
      sb.append("W\n");
    } else {
      sb.append("B\n");
    }

    if (board.header() != null) {
      FenHeader header = board.header();
      if (header.whiteKingCastling()) {
        sb.append("K");
      }
      if (header.whiteQueenCastling()) {
        sb.append("Q");
      }
      if (header.blackKingCastling()) {
        sb.append("k");
      }
      if (header.blackQueenCastling()) {
        sb.append("q");
      }
      if (!header.whiteKingCastling()
          && !header.whiteQueenCastling()
          && !header.blackKingCastling()
          && !header.blackQueenCastling()) {
        sb.append("-");
      }
      sb.append(" ");

      if (header.enPassant() != null) {
        sb.append(Move.positionToString(header.enPassant()));
      } else {
        sb.append("-");
      }
      sb.append(" ");
      sb.append(header.fiftyMoveRule()).append(" ");
      sb.append(header.playedMoves()).append(" ");
      sb.append("\n");
    }

    BoardRepresentation representation = board.board();
    for (int y = 7; y >= 0; y--) {
      for (int x = 0; x <= 7; x++) {
        ColoredPiece piece = representation.getPieceAt(x, y);
        sb.append(piece.getPiece().getCharRepresentation(piece.getColor() == Color.WHITE))
            .append(" ");
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
