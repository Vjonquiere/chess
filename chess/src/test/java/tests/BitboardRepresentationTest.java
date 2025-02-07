package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.utils.Position;

public class BitboardRepresentationTest {
  Bitboard DEFAULT_WHITE_KING = new Bitboard(16L);
  Bitboard DEFAULT_WHITE_QUEEN = new Bitboard(8L);
  Bitboard DEFAULT_WHITE_BISHOPS = new Bitboard(36L);
  Bitboard DEFAULT_WHITE_ROOKS = new Bitboard(129L);
  Bitboard DEFAULT_WHITE_KNIGHTS = new Bitboard(66L);
  Bitboard DEFAULT_WHITE_PAWNS = new Bitboard(65280L);
  Bitboard DEFAULT_BLACK_KING = new Bitboard(1152921504606846976L);
  Bitboard DEFAULT_BLACK_QUEEN = new Bitboard(576460752303423488L);
  Bitboard DEFAULT_BLACK_BISHOPS = new Bitboard(2594073385365405696L);
  // Bitboard DEFAULT_BLACK_ROOKS = new Bitboard(9295429630892703744L);
  Bitboard DEFAULT_BLACK_KNIGHT = new Bitboard(4755801206503243776L);
  Bitboard DEFAULT_BLACK_PAWNS = new Bitboard(71776119061217280L);

  @Test
  public void testGetPawns() {
    // Test with default positions
    BitboardRepresentation board = new BitboardRepresentation();

    int x = 0;
    int y = 6;
    // Black pawns initial positions
    for (Position position : board.getPawns(false)) {
      assertEquals(x++, position.getX());
      assertEquals(y, position.getY());
    }

    x = 0;
    y = 1;
    // White pawns initial positions
    for (Position position : board.getPawns(true)) {
      assertEquals(x++, position.getX());
      assertEquals(y, position.getY());
    }

    board.movePiece(new Position(1, 0), new Position(2, 0)); // move pawn
    List<Position> pawns = board.getPawns(true);
    assertFalse(pawns.contains(new Position(1, 0)));
    assertTrue(pawns.contains(new Position(2, 0)));
  }

  @Test
  public void testGetQueens() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetBishops() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetRooks() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetKnights() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetKing() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetNbRows() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertEquals(8, board.getNbRows());
  }

  @Test
  public void testGetNbCols() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertEquals(8, board.getNbCols());
  }

  @Test
  public void testGetPieceAt() {
    BitboardRepresentation board = new BitboardRepresentation();

    for (Position position :
        board.getPawns(true)) { // Test getPieceAt on white pawns at the beginning of the game
      assertEquals(
          new ColoredPiece<Piece, Color>(Piece.PAWN, Color.WHITE),
          board.getPieceAt(position.getX(), position.getY()));
    }
    for (Position position :
        board.getPawns(false)) { // Test getPieceAt on white pawns at the beginning of the game
      assertEquals(
          new ColoredPiece<Piece, Color>(Piece.PAWN, Color.BLACK),
          board.getPieceAt(position.getX(), position.getY()));
    }
    assertNotEquals(
        new ColoredPiece<Piece, Color>(Piece.ROOK, Color.WHITE),
        board.getPieceAt(0, 1)); // Not a rook a pawn place

    assertEquals(
        new ColoredPiece<>(Piece.EMPTY, Color.EMPTY), board.getPieceAt(0, 3)); // Empty square
    board.movePiece(new Position(1, 0), new Position(3, 0)); // moveUp pawn
    assertEquals(
        new ColoredPiece<>(Piece.PAWN, Color.WHITE), board.getPieceAt(0, 3)); // Check if pawn moved
  }

  @Test
  public void testMovePiece() {
    BitboardRepresentation board = new BitboardRepresentation();

    // Test move on white pawn
    assertEquals(new ColoredPiece<>(Piece.PAWN, Color.WHITE), board.getPieceAt(0, 1));
    board.movePiece(new Position(1, 0), new Position(3, 0));
    assertEquals(new ColoredPiece<>(Piece.EMPTY, Color.EMPTY), board.getPieceAt(0, 1));
    assertEquals(new ColoredPiece<>(Piece.PAWN, Color.WHITE), board.getPieceAt(0, 3));

    // Test move on black knight
    assertEquals(new ColoredPiece<>(Piece.KNIGHT, Color.BLACK), board.getPieceAt(1, 7));
    board.movePiece(new Position(7, 1), new Position(5, 0));
    assertEquals(new ColoredPiece<>(Piece.EMPTY, Color.EMPTY), board.getPieceAt(1, 7));
    assertEquals(new ColoredPiece<>(Piece.KNIGHT, Color.BLACK), board.getPieceAt(0, 5));
  }

  @Test
  public void testGetAvailableMoves() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertEquals(List.of(), board.getAvailableMoves(4, 0, false)); // King is blocked
    assertEquals(List.of(), board.getAvailableMoves(3, 0, false)); // Queen is blocked
    assertEquals(List.of(), board.getAvailableMoves(0, 0, false)); // Rook is blocked
    assertEquals(
        List.of(
            new Move(new Position(0, 6), new Position(2, 5)),
            new Move(new Position(0, 6), new Position(2, 7))),
        board.getAvailableMoves(6, 0, false)); // Knight move

    board.movePiece(new Position(1, 0), new Position(2, 0)); // move pawn
    assertEquals(
        List.of(new Move(new Position(2, 0), new Position(3, 0))),
        board.getAvailableMoves(0, 2, false)); // pawn move
    assertEquals(
        List.of(new Move(new Position(0, 0), new Position(1, 0))),
        board.getAvailableMoves(0, 0, false)); // Rook no more blocked
  }

  @Test
  public void testIsCheck() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertFalse(board.isCheck(Color.BLACK));
    assertFalse(board.isCheck(Color.WHITE));

    board =
        new BitboardRepresentation(
            new Bitboard(1048576L),
            DEFAULT_WHITE_QUEEN,
            DEFAULT_WHITE_BISHOPS,
            DEFAULT_WHITE_ROOKS,
            DEFAULT_WHITE_KNIGHTS,
            DEFAULT_WHITE_PAWNS,
            DEFAULT_BLACK_KING,
            DEFAULT_BLACK_QUEEN,
            DEFAULT_BLACK_BISHOPS,
            new Bitboard(17592186044416L),
            DEFAULT_BLACK_KNIGHT,
            DEFAULT_BLACK_PAWNS);
    assertTrue(board.isCheck(Color.WHITE));
    assertFalse(board.isCheck(Color.BLACK));
    assertFalse(board.isCheckMate(Color.WHITE));
    board.movePiece(new Position(2, 4), new Position(0, 4)); // move king
    board.movePiece(new Position(0, 3), new Position(2, 0)); // move queen
    board.movePiece(new Position(0, 5), new Position(2, 6));
    board.movePiece(new Position(1, 4), new Position(0, 3));
    board.movePiece(new Position(1, 0), new Position(0, 5));
    board.movePiece(new Position(0, 6), new Position(3, 0));
    board.movePiece(new Position(2, 6), new Position(0, 6));
    board.movePiece(new Position(2, 0), new Position(7, 0));
    System.out.println(board.getPieceAt(4, 2).getPiece());
    assertTrue(board.isCheckMate(Color.WHITE));
  }

  @Test
  public void testIsCheckMate() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertFalse(board.isCheckMate(Color.BLACK));
    assertFalse(board.isCheckMate(Color.WHITE));
  }
}
