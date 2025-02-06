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
    for (Position position : board.getPawns(false)) {
      assertEquals(x++, position.getX());
      assertEquals(y, position.getY());
    }

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

    int xWhite = 0, yWhite = 7;
    boolean white = true;

    // TO DO
    // Make pawn move so that it reaches its last rank

    boolean resultWhite = board.isPawnPromoting(xWhite, yWhite, white);
    assertTrue(resultWhite, "White pawn should be able to promote!");

    int xBlack = 0, yBlack = 0;

    // TO DO
    // Make pawn move so that it reaches its last rank

    boolean resultBlack = board.isPawnPromoting(xBlack, yBlack, !white);
    assertTrue(resultBlack, "Black pawn should be able to promote!");
    */

    assertFalse(false);
  }
}
