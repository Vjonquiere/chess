package pdp.model.savers;

import pdp.model.board.BoardRepresentation;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;

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

    BoardRepresentation representation = board.board();
    for (int y = 7; y >= 0; y--) {
      for (int x = 0; x <= 7; x++) {
        ColoredPiece piece = representation.getPieceAt(x, y);
        sb.append(piece.piece.getCharRepresentation(piece.color == Color.WHITE)).append(" ");
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
