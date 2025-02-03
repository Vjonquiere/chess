package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import pdp.model.BitboardRepresentation;
import pdp.model.BoardRepresentation;
import pdp.utils.Position;

public class BoardTest {

  @Test
  public void testGetPawns() {
    // Test with default positions
    BoardRepresentation board = new BitboardRepresentation();
    int x = 0;
    int y = 6;
    for (Position position : board.getPawns(false)) {
      assertEquals(x++, position.getX());
      assertEquals(y, position.getY());
    }
  }
}
