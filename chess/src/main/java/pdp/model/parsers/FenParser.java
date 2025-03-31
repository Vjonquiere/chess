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

/** File parser that produce boards from file at FEN format. */
public final class FenParser {
  /** Map making correspond a string and the piece it represents. */
  private static final Map<String, ColoredPiece> PIECES =
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

  /** Bitboard to initialize when parsing a file. */
  private static final BitboardRepresentation BITBOARD_REP =
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

  /** Private constructor to avoid instantiation. */
  private FenParser() {}

  /**
   * Loads a board position from a FEN string.
   *
   * @param fen The FEN string representing the board state
   * @return A FileBoard representing the loaded position
   */
  public static FileBoard loadBoardFromFen(final String fen) {
    final String[] parts = fen.split(" ");

    if (parts.length != 6) {
      throw new IllegalArgumentException("Invalid FEN format");
    }

    final String[] ranks = parts[0].split("/");
    if (ranks.length != 8) {
      throw new IllegalArgumentException("Invalid FEN board structure");
    }

    for (int y = 7; y >= 0; y--) {
      int x = 0;
      for (final char c : ranks[7 - y].toCharArray()) {
        if (Character.isDigit(c)) {
          x += Character.getNumericValue(c); // Skip empty squares
        } else {
          final ColoredPiece piece = PIECES.get(String.valueOf(c));
          BITBOARD_REP.setSquare(piece, (x + y * 8));
          x++;
        }
      }
    }

    final boolean isWhiteTurn = parts[1].equals("w");
    boolean whiteKingCastling = false;
    boolean whiteQueenCastling = false;
    boolean blackKingCastling = false;
    boolean blackQueenCastling = false;

    for (final char c : parts[2].toCharArray()) {
      switch (c) {
        case 'K' -> whiteKingCastling = true;
        case 'Q' -> whiteQueenCastling = true;
        case 'k' -> blackKingCastling = true;
        case 'q' -> blackQueenCastling = true;
        default -> throw new RuntimeException("Unknown castling right");
      }
    }

    final Position pos = parts[3].equals("-") ? null : Move.stringToPosition(parts[3]);

    final int fiftyMoveRule = Integer.parseInt(parts[4]);
    final int playedMoves = Integer.parseInt(parts[5].trim());

    final FenHeader header =
        new FenHeader(
            whiteKingCastling,
            whiteQueenCastling,
            blackKingCastling,
            blackQueenCastling,
            pos,
            fiftyMoveRule,
            playedMoves);
    return new FileBoard(BITBOARD_REP, isWhiteTurn, header);
  }
}
