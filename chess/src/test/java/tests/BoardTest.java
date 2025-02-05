package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.Test;
import pdp.model.Bitboard;
import pdp.model.BitboardRepresentation;
import pdp.utils.Position;

public class BoardTest {

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



    //board.getInlineMoves(new Position(3,3), new Bitboard(289360691367707652L), new Bitboard(1157443791906410512L));
    //board.getDiagonalMoves(new Position(3,3), new Bitboard(0L), new Bitboard(1157443791906410512L));
    //board.getPawnMoves(new Position(1,0), new Bitboard(0L), new Bitboard(1157443791906541584L));
    //board.getQueenMoves(new Position(4,4), new Bitboard(34628173824L), new Bitboard(17609500131328L));
  }
}
