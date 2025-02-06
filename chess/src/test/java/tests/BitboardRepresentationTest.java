package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;
import pdp.model.BitboardRepresentation;
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
    assertEquals(List.of(), board.getAvailableMoves(6, 0, null)); // Knight move

    // board.getInlineMoves(new Position(3,3), new Bitboard(289360691367707652L), new
    // Bitboard(1157443791906410512L));
    // board.getDiagonalMoves(new Position(3,3), new Bitboard(0L), new
    // Bitboard(1157443791906410512L));
    // board.getPawnMoves(new Position(1,0), new Bitboard(0L), new Bitboard(1157443791906541584L));
    // board.getQueenMoves(new Position(4,4), new Bitboard(34628173824L), new
    // Bitboard(17609500131328L));
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
    /*
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    Position whitePawnSrcPos = new Position(6, 0);
    Position whitePawnDstPos = new Position(7, 0);
    // MOVE PIECE BLOCKING DST POSITION BEFORE MOVING THE PAWN
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);

    boolean resultWhite = board.isPawnPromoting(0, 7, white);
    assertTrue(resultWhite, "White pawn should be able to promote !");

    Position blackPawnSrcPos = new Position(1, 0);
    Position blackPawnDstPos = new Position(0, 0);
    // MOVE PIECE BLOCKING DST POSITION BEFORE MOVING THE PAWN
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);

    boolean resultBlack = board.isPawnPromoting(0, 0, !white);
    assertTrue(resultBlack, "Black pawn should be able to promote !");
    */
  }

  @Test
  public void testPromotePawnShouldBeSuccess() {
    /*
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    Position whitePawnSrcPos = new Position(0, 6);
    Position whitePawnDstPos = new Position(0, 7);
    // MOVE PIECE BLOCKING DST POSITION BEFORE MOVING THE PAWN
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);
    board.promotePawn(0, 7, white, Piece.QUEEN);

    assertNotNull(board.getPieceAt(0, 7));
    assertEquals(Piece.QUEEN, board.getPieceAt(0, 7).getPiece());

    Position blackPawnSrcPos = new Position(0, 1);
    Position blackPawnDstPos = new Position(0, 0);
    // MOVE PIECE BLOCKING DST POSITION BEFORE MOVING THE PAWN
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);
    board.promotePawn(0, 0, !white, Piece.QUEEN);

    assertNotNull(board.getPieceAt(0, 0));
    assertEquals(Piece.QUEEN, board.getPieceAt(0, 0).getPiece());
    */
  }

  @Test
  public void testPromotePawnShouldBeFailure() {
    /*
    BitboardRepresentation board = new BitboardRepresentation();

    // Should not be possible to promote a pawn to a pawn or to a king
    assertThrows(IllegalArgumentException.class, () -> board.promotePawn(0, 0, true, Piece.KING));
    assertThrows(IllegalArgumentException.class, () -> board.promotePawn(0, 0, true, Piece.PAWN));
    */
  }

  @Test
  public void testPromotePawnShouldNotWorkForOtherPieces() {
    /*
    BitboardRepresentation board = new BitboardRepresentation();

    // Should not change bitboards
    board.promotePawn(0,0,true,Piece.QUEEN);

    // Check bitboards didn't change and available moves correspond to the correct ones

    board.promotePawn(0,7,false,Piece.QUEEN);

    // Check bitboards didn't change and available moves correspond to the correct ones

    */
  }
}
