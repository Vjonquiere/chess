package pdp.model.parsers;

import static java.util.Map.entry;

import java.util.Map;
import pdp.model.board.Bitboard;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class FENparser {
  private static final Map<String, ColoredPiece> pieces =
      Map.ofEntries(
          entry("K", new ColoredPiece(Piece.KING, Color.WHITE)),
          entry("Q", new ColoredPiece(Piece.QUEEN, Color.WHITE)),
          entry("B", new ColoredPiece(Piece.BISHOP, Color.WHITE)),
          entry("R", new ColoredPiece(Piece.ROOK, Color.WHITE)),
          entry("N", new ColoredPiece(Piece.KNIGHT, Color.WHITE)),
          entry("P", new ColoredPiece(Piece.PAWN, Color.WHITE)),
          entry("k", new ColoredPiece(Piece.KING, Color.BLACK)),
          entry("q", new ColoredPiece(Piece.QUEEN, Color.BLACK)),
          entry("b", new ColoredPiece(Piece.BISHOP, Color.BLACK)),
          entry("r", new ColoredPiece(Piece.ROOK, Color.BLACK)),
          entry("n", new ColoredPiece(Piece.KNIGHT, Color.BLACK)),
          entry("p", new ColoredPiece(Piece.PAWN, Color.BLACK)));
  private static final BitboardRepresentation bitboardRepresentation =
      new BitboardRepresentation(
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L),
          new Bitboard(0L));

  /**
   * Loads a board position from a FEN string.
   *
   * @param fen The FEN string representing the board state
   * @return A FileBoard representing the loaded position
   */
  public static FileBoard loadBoardFromFen(String fen) {
    String[] parts = fen.split(" ");

    if (parts.length != 6) {
      throw new IllegalArgumentException("Invalid FEN format");
    }

    String[] ranks = parts[0].split("/");
    if (ranks.length != 8) {
      throw new IllegalArgumentException("Invalid FEN board structure");
    }

    for (int y = 7; y >= 0; y--) {
      int x = 0;
      for (char c : ranks[7 - y].toCharArray()) {
        if (Character.isDigit(c)) {
          x += Character.getNumericValue(c); // Skip empty squares
        } else {
          ColoredPiece piece = pieces.get(String.valueOf(c));
          bitboardRepresentation.setSquare(piece, (x + (y * 8)));
          x++;
        }
      }
    }

    boolean isWhiteTurn = parts[1].equals("w");
    boolean whiteKingCastling = false;
    boolean whiteQueenCastling = false;
    boolean blackKingCastling = false;
    boolean blackQueenCastling = false;

    for (char c : parts[2].toCharArray()) {
      switch (c) {
        case 'K' -> whiteKingCastling = true;
        case 'Q' -> whiteQueenCastling = true;
        case 'k' -> blackKingCastling = true;
        case 'q' -> blackQueenCastling = true;
      }
    }

    Position pos = parts[3].equals("-") ? null : Move.stringToPosition(parts[3]);

    int fiftyMoveRule = (Integer.parseInt(parts[4]));
    int playedMoves = (Integer.parseInt(parts[5].trim()));

    FenHeader header =
        new FenHeader(
            whiteKingCastling,
            whiteQueenCastling,
            blackKingCastling,
            blackQueenCastling,
            pos,
            fiftyMoveRule,
            playedMoves);
    return new FileBoard(bitboardRepresentation, isWhiteTurn, header);
  }
}
