package pdp.model.parsers;

import static java.util.Map.entry;

import java.util.Map;
import java.util.Objects;
import pdp.BoardLoaderBaseListener;
import pdp.BoardLoaderParser;
import pdp.model.board.Bitboard;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

/** Class that use parser generator class to generate boards from parsing trees. */
public class BoardLoaderListener extends BoardLoaderBaseListener {
  /** Map making correspond a string and the piece it represents. */
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

  /** Bitboard to initialize when parsing a file. */
  private final BitboardRepresentation bitboardRepresentation =
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

  /** Row at the start of the parsing. */
  private int y = 8;

  /** Column at the start of the parsing. */
  private int x = 0;

  /** Boolean to indicate whether it is the white player's turn or not. */
  private boolean whiteTurn;

  /** Fen Header to parse. */
  private FenHeader fenHeader;

  /** Boolean to indicate whether the white short castle is possible or not. */
  private boolean whiteKingCastling;

  /** Boolean to indicate whether the white long castle is possible or not. */
  private boolean whiteQueenCastling;

  /** Boolean to indicate whether the black short castle is possible or not. */
  private boolean blackKingCastling;

  /** Boolean to indicate whether the black long castle is possible or not. */
  private boolean blackQueenCastling;

  /** Position of the possible en passant take. */
  private Position enPassant;

  /** Number of moves made with no capture or no pawn move. */
  private int fiftyMoveRule;

  /** Number of moves played since the beginning of the game. */
  private int movePlayed;

  /**
   * Get the result of the parsing.
   *
   * @return The board and current player parsed
   */
  public FileBoard getResult() {
    return new FileBoard(bitboardRepresentation, whiteTurn, fenHeader);
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

  @Override
  public void enterCastling(BoardLoaderParser.CastlingContext ctx) {
    whiteKingCastling = ctx.WHITE_KING() != null;
    whiteQueenCastling = ctx.WHITE_QUEEN() != null;
    blackKingCastling = ctx.BLACK_KING() != null;
    blackQueenCastling = ctx.BLACK_QUEEN() != null;
  }

  @Override
  public void enterFen(BoardLoaderParser.FenContext ctx) {
    enPassant =
        ctx.CHESS_SQUARE() == null ? null : Move.stringToPosition(ctx.CHESS_SQUARE().getText());
    fiftyMoveRule = ctx.INT(0) == null ? 0 : Integer.parseInt(ctx.INT(0).getText());
    movePlayed = ctx.INT(1) == null ? 0 : Integer.parseInt(ctx.INT(1).getText());
  }

  @Override
  public void exitFen(BoardLoaderParser.FenContext ctx) {
    fenHeader =
        new FenHeader(
            whiteKingCastling,
            whiteQueenCastling,
            blackKingCastling,
            blackQueenCastling,
            enPassant,
            fiftyMoveRule,
            movePlayed);
  }
}
