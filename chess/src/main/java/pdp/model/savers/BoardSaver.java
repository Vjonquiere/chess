package pdp.model.savers;

import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.parsers.FenHeader;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;

/** Save board to custom format. */
public final class BoardSaver {

  /** Private constructor to avoid instanciating a utility class. */
  private BoardSaver() {}

  /**
   * Generate the String corresponding to the current player and board. (This String can be directly
   * load by the board parser)
   *
   * @param board The board and current player to save
   * @return The board and current player String
   */
  public static String saveBoard(final FileBoard board) {
    final StringBuilder builder = new StringBuilder();
    // Save current player
    if (board.isWhiteTurn()) {
      builder.append("W\n");
    } else {
      builder.append("B\n");
    }

    if (board.header() != null) {
      final FenHeader header = board.header();
      if (header.whiteKingCastling()) {
        builder.append('K');
      }
      if (header.whiteQueenCastling()) {
        builder.append('Q');
      }
      if (header.blackKingCastling()) {
        builder.append('k');
      }
      if (header.blackQueenCastling()) {
        builder.append('q');
      }
      if (!header.whiteKingCastling()
          && !header.whiteQueenCastling()
          && !header.blackKingCastling()
          && !header.blackQueenCastling()) {
        builder.append('-');
      }
      builder.append(' ');

      if (header.enPassant() != null) {
        builder.append(Move.positionToString(header.enPassant()));
      } else {
        builder.append('-');
      }
      builder.append(' ');
      builder.append(header.fiftyMoveRule()).append(' ');
      builder.append(header.playedMoves()).append(' ');
      builder.append('\n');
    }

    final BoardRepresentation representation = board.board();
    for (int y = 7; y >= 0; y--) {
      for (int x = 0; x <= 7; x++) {
        final ColoredPiece piece = representation.getPieceAt(x, y);
        builder
            .append(piece.getPiece().getCharRepresentation(piece.getColor() == Color.WHITE))
            .append(' ');
      }
      builder.append('\n');
    }
    return builder.toString();
  }
}
