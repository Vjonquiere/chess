package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.utils.Position;

public class BitboardRepresentationTest {

  @Test
  public void testGetPawns() {
    // Test with default positions
    BitboardRepresentation board = new BitboardRepresentation();

    /*Method method = BitboardRepresentation.class.getDeclaredMethod("getQueenMoves", Position.class, Bitboard.class, Bitboard.class);
    method.setAccessible(true);
    method.invoke(board, new)*/

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

    board.movePiece(new Position(1, 0), new Position(2, 0));

    assertEquals(List.of(), board.getAvailableMoves(4, 0, null)); // King move
    assertEquals(List.of(), board.getAvailableMoves(3, 0, null)); // Queen move

    // assertEquals(new Move(new Position(0,5), new Position(0, 3)), new Move(new Position(0,5), new
    // Position(0, 3)));
    // assertIterableEquals(List.of(new Move(new Position(0,6), new Position(2,5)), new Move(new
    // Position(0,6), new Position(2,7))), board.getAvailableMoves(6, 0, null)); // Knight move

    // board.getInlineMoves(new Position(3,3), new Bitboard(289360691367707652L), new
    // Bitboard(1157443791906410512L));
    // board.getDiagonalMoves(new Position(3,3), new Bitboard(0L), new
    // Bitboard(1157443791906410512L));
    // board.getPawnMoves(new Position(1,0), new Bitboard(0L), new Bitboard(1157443791906541584L));
    // board.getQueenMoves(new Position(4,4), new Bitboard(34628173824L), new
    // Bitboard(17609500131328L));
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
}
