package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import pdp.model.Bitboard;
import pdp.model.BitboardRepresentation;
import pdp.model.Board;
import pdp.utils.Position;

import java.util.List;

public class BoardTest {

  @Test
  public void testGetPawns() {
    // Test with default positions
    BitboardRepresentation board = new BitboardRepresentation();
    int x = 0;
    int y = 6;
    for (Position position : board.getPawns(false)) {
      assertEquals(x++, position.getX());
      assertEquals(y, position.getY());
    }

    assertEquals(List.of(), board.getAvailableMoves(4,0, null)); // King move
    assertEquals(List.of(), board.getAvailableMoves(3,0, null)); // Queen move
    assertEquals(List.of(), board.getAvailableMoves(6,0, null)); // Knight move
  }
}
