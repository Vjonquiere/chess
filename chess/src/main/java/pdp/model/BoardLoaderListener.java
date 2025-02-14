package pdp.model;

import static java.util.Map.entry;

import java.util.Map;
import java.util.Objects;
import pdp.BoardLoaderBaseListener;
import pdp.BoardLoaderParser;

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
  int y = 8;
  int x = 0;

  public BitboardRepresentation getResult() {
    return bitboardRepresentation;
  }

  @Override
  public void enterPlayer(BoardLoaderParser.PlayerContext ctx) {
    if (Objects.equals(ctx.PLAYER_COLOR().getText(), "W")) {
      // Set current player to white
      return;
    }
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
    bitboardRepresentation.setSquare(pieces.get(ctx.getText()), x + (y * 8));
    x++;
  }
}
