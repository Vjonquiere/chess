package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import pdp.model.ai.AIMove;
import pdp.model.board.Move;
import pdp.utils.Position;

public class AIMoveTest {

  @Test
  public void aiMoveTest() {
    AIMove aiMove = new AIMove(new Move(new Position(4, 1), new Position(4, 3)), 12);

    assertEquals(new Move(new Position(4, 1), new Position(4, 3)), aiMove.move());
    assertEquals(12, aiMove.score());
  }
}
