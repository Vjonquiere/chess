package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pdp.model.parsers.FenParser;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class FenParserTest {

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  public void testParseClassicBoard() {
    FileBoard board =
        FenParser.loadBoardFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    assertEquals(new ColoredPiece(Piece.KING, Color.WHITE), board.board().getPieceAt(4, 0));
    assertEquals(new ColoredPiece(Piece.KING, Color.BLACK), board.board().getPieceAt(4, 7));
    assertEquals(new ColoredPiece(Piece.ROOK, Color.WHITE), board.board().getPieceAt(0, 0));
    assertTrue(board.header().blackKingCastling());
    assertTrue(board.header().blackQueenCastling());
    assertTrue(board.header().whiteKingCastling());
    assertTrue(board.header().whiteQueenCastling());
    assertNull(board.header().enPassant());
    assertEquals(1, board.header().playedMoves());
    assertEquals(0, board.header().fiftyMoveRule());
    assertTrue(board.isWhiteTurn());
  }

  @Test
  public void testParseEnPassantBoard() {
    FileBoard board =
        FenParser.loadBoardFromFen(
            "rnbqkbnr/ppp1p1pp/8/4Pp2/3p4/8/PPPP1PPP/RNBQKBNR b KQkq f6 2 5");
    assertEquals(new ColoredPiece(Piece.PAWN, Color.BLACK), board.board().getPieceAt(5, 4));
    assertEquals(new ColoredPiece(Piece.PAWN, Color.WHITE), board.board().getPieceAt(4, 4));
    assertTrue(board.header().blackKingCastling());
    assertTrue(board.header().blackQueenCastling());
    assertTrue(board.header().whiteKingCastling());
    assertTrue(board.header().whiteQueenCastling());
    assertEquals(new Position(5, 5), board.header().enPassant());
    assertEquals(5, board.header().playedMoves());
    assertEquals(2, board.header().fiftyMoveRule());
    assertFalse(board.isWhiteTurn());
  }

  @Test
  public void testParseSomeCastlingEnable() {
    FileBoard board =
        FenParser.loadBoardFromFen(
            "rnbqkbn1/ppp1p1pr/7p/4Pp2/P2p3P/8/RPPP1PPR/1NBQKBN1 w q - 10 21");
    assertFalse(board.header().blackKingCastling());
    assertTrue(board.header().blackQueenCastling());
    assertFalse(board.header().whiteKingCastling());
    assertFalse(board.header().whiteQueenCastling());
    assertNull(board.header().enPassant());
    assertEquals(21, board.header().playedMoves());
    assertEquals(10, board.header().fiftyMoveRule());
    assertTrue(board.isWhiteTurn());
  }

  @Test
  public void testParseCheckBoard() {
    FileBoard board =
        FenParser.loadBoardFromFen(
            "rnbqkbn1/ppp3pr/2p3Qp/4P3/P2p3P/5p2/RPPP1PPR/1NB1KBN1 b q - 0 1");
    assertFalse(board.board().isCheck(Color.WHITE));
    assertTrue(board.board().isCheck(Color.BLACK));
  }

  @Test
  public void testNotAnFenBoard() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          FenParser.loadBoardFromFen("Not and FEN board");
        });
  }

  @Test
  public void testNotAnFenBoardMissingArg() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          FenParser.loadBoardFromFen(
              "rnbqkbn1/ppp3pr/2p3Qp/4P3/P2p3P/5p2/RPPP1PPR/1NB1KBN1 b q 0 1");
        });
  }

  @Test
  public void testNotAnFenBoardMissingBoardLine() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          FenParser.loadBoardFromFen("rnbqkbn1/ppp3pr/2p3Qp/4P3/5p2/RPPP1PPR/1NB1KBN1 b q - 0 1");
        });
  }

  @Test
  public void testInvalidCastlingRights() {
    assertThrows(
        RuntimeException.class,
        () -> {
          FenParser.loadBoardFromFen(
              "rnbqkbn1/ppp3pr/2p3Qp/4P3/P2p3P/5p2/RPPP1PPR/1NB1KBN1 b qA - 0 1");
        });
  }
}
