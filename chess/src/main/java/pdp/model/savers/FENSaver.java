package pdp.model.savers;

import java.util.Objects;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.parsers.FenHeader;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;

public class FENSaver {
  private int empty = 0;

  /**
   * Generate the FEN string representing the current board position.
   *
   * @param board The board and current player to save
   * @return The FEN string
   */
  public static String saveBoard(FileBoard board) {
    StringBuilder sb = new StringBuilder();
    BoardRepresentation representation = board.board();

    for (int y = 7; y >= 0; y--) {
      int emptyCount = 0;
      for (int x = 0; x <= 7; x++) {
        ColoredPiece piece = representation.getPieceAt(x, y);
        if (Objects.equals(piece, new ColoredPiece(Piece.EMPTY, Color.EMPTY))) {
          emptyCount++;
        } else {
          if (emptyCount > 0) {
            sb.append(emptyCount);
            emptyCount = 0;
          }
          sb.append(piece.getPiece().getCharRepresentation(piece.getColor() == Color.WHITE));
        }
      }
      if (emptyCount > 0) {
        sb.append(emptyCount);
      }
      if (y > 0) {
        sb.append("/");
      }
    }

    sb.append(" ").append(board.isWhiteTurn() ? "w" : "b");

    if (board.header() != null) {
      FenHeader header = board.header();
      String castling = "";
      if (header.whiteKingCastling()) castling += "K";
      if (header.whiteQueenCastling()) castling += "Q";
      if (header.blackKingCastling()) castling += "k";
      if (header.blackQueenCastling()) castling += "q";
      sb.append(" ").append(castling.isEmpty() ? "-" : castling);

      sb.append(" ")
          .append(header.enPassant() != null ? Move.positionToString(header.enPassant()) : "-");

      sb.append(" ").append(header.fiftyMoveRule()).append(" ").append(header.playedMoves());
    } else {
      sb.append(" - - 0 1");
    }

    return sb.toString();
  }
}
