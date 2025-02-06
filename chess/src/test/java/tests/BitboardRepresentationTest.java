package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.model.Piece;
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

  @Test
  public void testIsPawnPromotingShouldReturnFalse() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean resultWhite;
    boolean resultBlack;
    boolean white = true;
    int xForWhite = 0, yForWhite = 0;
    int xForBlack = 0, yForBlack = 7;
    int nbLoops = 7;

    for (int i = 0; i < nbLoops; i++) {
      resultWhite = board.isPawnPromoting(xForWhite, yForWhite, white);
      resultBlack = board.isPawnPromoting(xForBlack, yForBlack, !white);
      assertFalse(resultWhite, "Pawn should not be able to promote !");
      assertFalse(resultBlack, "Pawn should not be able to promote !");
      yForWhite++;
      yForBlack--;
    }
  }

  @Test
  public void testIsPawnPromotingShouldReturnTrue() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    // Move piece blocking the last rank position before moving the white pawn
    Position whiteBlockerCurrPos = new Position(7, 0);
    Position whiteBlockerNextPos = new Position(4, 0);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(1, 0);
    Position whitePawnDstPos = new Position(7, 0);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);

    boolean resultWhite = board.isPawnPromoting(0, 7, white);
    assertTrue(resultWhite, "White pawn should be able to promote !");

    // Same thing for black
    Position blackBlockerCurrPos = new Position(0, 7);
    Position blackBlockerNextPos = new Position(3, 7);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(6, 7);
    Position blackPawnDstPos = new Position(0, 7);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);

    boolean resultBlack = board.isPawnPromoting(7, 0, !white);
    assertTrue(resultBlack, "Black pawn should be able to promote !");
  }

  @Test
  public void testPromotePawnShouldBeSuccess() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    // Move piece blocking the last rank position before moving the white pawn
    Position whiteBlockerCurrPos = new Position(7, 0);
    Position whiteBlockerNextPos = new Position(4, 0);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(1, 0);
    Position whitePawnDstPos = new Position(7, 0);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);
    board.promotePawn(0, 7, white, Piece.QUEEN);

    assertNotNull(board.getPieceAt(0, 7));
    assertEquals(Piece.QUEEN, board.getPieceAt(0, 7).getPiece());

    // Same thing for black
    Position blackBlockerCurrPos = new Position(0, 7);
    Position blackBlockerNextPos = new Position(3, 7);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(6, 7);
    Position blackPawnDstPos = new Position(0, 7);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);
    board.promotePawn(7, 0, !white, Piece.QUEEN);

    assertNotNull(board.getPieceAt(7, 0));
    assertEquals(Piece.QUEEN, board.getPieceAt(7, 0).getPiece());
  }

  @Test
  public void testPromotePawnShouldBeFailure() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    // Move piece blocking the last rank position before moving the white pawn
    Position whiteBlockerCurrPos = new Position(7, 0);
    Position whiteBlockerNextPos = new Position(4, 0);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(1, 0);
    Position whitePawnDstPos = new Position(7, 0);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);

    // Ensure pawn is remaining at the promotion position before trying invalid promotion
    assertEquals(
        Piece.PAWN,
        board.getPieceAt(0, 7).getPiece(),
        "White pawn should still be at promotion square before invalid promotion !");

    // Attempt invalid promotions
    board.promotePawn(0, 7, white, Piece.KING);
    board.promotePawn(0, 7, white, Piece.PAWN);

    assertEquals(
        Piece.PAWN,
        board.getPieceAt(0, 7).getPiece(),
        "White pawn should remain unchanged after invalid promotion !");

    // Same process for black
    Position blackBlockerCurrPos = new Position(0, 7);
    Position blackBlockerNextPos = new Position(3, 7);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(6, 7);
    Position blackPawnDstPos = new Position(0, 7);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);

    assertEquals(
        Piece.PAWN,
        board.getPieceAt(7, 0).getPiece(),
        "Black pawn should still be at promotion square before invalid promotion!");

    board.promotePawn(7, 0, !white, Piece.KING);
    board.promotePawn(7, 0, !white, Piece.PAWN);

    assertEquals(
        Piece.PAWN,
        board.getPieceAt(7, 0).getPiece(),
        "Black pawn should remain unchanged after invalid promotion!");
  }

  @Test
  public void testPromotePawnShouldNotWorkForOtherPieces() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    Position whiteBlockerCurrPos = new Position(7, 0);
    Position whiteBlockerNextPos = new Position(4, 0);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move a white knight to the last rank
    Position whiteKnightSrcPos = new Position(0, 1);
    Position whiteKnightDstPos = new Position(7, 0);
    board.movePiece(whiteKnightSrcPos, whiteKnightDstPos);

    // Try to promote the knight
    board.promotePawn(0, 7, white, Piece.QUEEN);

    assertEquals(
        Piece.KNIGHT, board.getPieceAt(0, 7).getPiece(), "White knight should not be promotable !");

    // Same for black but with a bishop for instance
    Position blackBlockerCurrPos = new Position(0, 7);
    Position blackBlockerNextPos = new Position(3, 7);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    // Move a black bishop to the first rank
    Position blackBishopSrcPos = new Position(7, 2);
    Position blackBishopDstPos = new Position(0, 7);
    board.movePiece(blackBishopSrcPos, blackBishopDstPos);

    // Try to promote the bishop
    board.promotePawn(7, 0, !white, Piece.QUEEN);

    assertEquals(
        Piece.BISHOP, board.getPieceAt(7, 0).getPiece(), "Black bishop should not be promotable !");
  }
}
