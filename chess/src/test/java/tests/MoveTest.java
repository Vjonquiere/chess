package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.model.board.Move;
import pdp.utils.Position;

public class MoveTest {

  @Test
  public void testEquals_sameMove() {
    Position source1 = new Position(4, 1); // "e2"
    Position dest1 = new Position(4, 3); // "e4"
    Move move1 = new Move(source1, dest1, null, true);

    Position source2 = new Position(4, 1); // "e2"
    Position dest2 = new Position(4, 3); // "e4"
    Move move2 = new Move(source2, dest2);

    assertTrue(move1.equals(move2));
  }

  @Test
  public void testEquals_differentMove() {
    Position source1 = new Position(4, 1); // "e2"
    Position dest1 = new Position(4, 3); // "e4"
    Move move1 = new Move(source1, dest1);

    Position source3 = new Position(1, 0); // "b1"
    Position dest3 = new Position(1, 2); // "b3"
    Move move3 = new Move(source3, dest3);

    assertFalse(move1.equals(move3));
  }

  @Test
  public void testEquals_null() {
    Position source1 = new Position(4, 1); // "e2"
    Position dest1 = new Position(4, 3); // "e4"
    Move move1 = new Move(source1, dest1);

    assertFalse(move1.equals(null));
  }

  @Test
  public void testEquals_differentClass() {
    Position source1 = new Position(4, 1); // "e2"
    Position dest1 = new Position(4, 3); // "e4"
    Move move1 = new Move(source1, dest1);

    assertFalse(move1.equals(new Object()));
  }

  @Test
  public void testFromString_validMove() {
    Move move = Move.fromString("e2-e4");
    Position expectedSource = new Position(4, 1); // "e2"
    Position expectedDest = new Position(4, 3); // "e4"

    assertEquals(expectedSource, move.getSource());
    assertEquals(expectedDest, move.getDest());
  }

  @Test
  public void testToString() {
    Position source = new Position(4, 1); // "e2"
    Position dest = new Position(4, 3); // "e4"
    Move move = new Move(source, dest);

    assertEquals("e2-e4", move.toString());
  }

  @Test
  public void testStringToPosition() {
    Position position = Move.stringToPosition("e4");
    Position expected = new Position(4, 3); // "e4" corresponds to (3, 4)

    assertEquals(expected, position);
  }
}
