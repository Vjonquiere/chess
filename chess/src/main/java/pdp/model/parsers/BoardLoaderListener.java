package pdp.model.parsers;

import static java.util.Map.entry;

import java.util.Map;
import java.util.Objects;
import pdp.BoardLoaderBaseListener;
import pdp.BoardLoaderParser;
import pdp.model.board.Bitboard;
import pdp.model.board.BitboardRepresentation;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;

public class BoardLoaderListener extends BoardLoaderBaseListener {
  private static Map<String, ColoredPiece> pieces =
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
  private BitboardRepresentation bitboardRepresentation =
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
  private int y = 8;
  private int x = 0;
  private boolean whiteTurn;

  /**
   * Get the result of the parsing
   *
   * @return The board and current player parsed
   */
  public FileBoard getResult() {
    return new FileBoard(bitboardRepresentation, whiteTurn);
  }

  @Override
  public void enterPlayer(BoardLoaderParser.PlayerContext ctx) {
    whiteTurn = Objects.equals(ctx.PLAYER_COLOR().getText(), "W");
  }

  @Override
  public void enterBoardLine(BoardLoaderParser.BoardLineContext ctx) {
    y--;
    x = 0;
  }

  @Override
  public void enterPiece(BoardLoaderParser.PieceContext ctx) {
    if (ctx.getText().equals("_")) {
      x++;
      return;
    }
    ColoredPiece piece = pieces.get(ctx.getText());
    int square = (x + (y * 8));
    if (piece == null) {
      throw new RuntimeException(
          "Piece `" + ctx.getText() + "` at square " + square + " is not recognized");
    } else {
      bitboardRepresentation.setSquare(piece, square);
    }
    x++;
  }
}
