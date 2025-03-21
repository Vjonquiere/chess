package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import pdp.model.ai.AiMove;
import pdp.model.board.Move;
import pdp.utils.Position;

public class AiMoveTest {

  @Test
  public void aiMoveTest() {
    AiMove aiMove = new AiMove(new Move(new Position(4, 1), new Position(4, 3)), 12);

    assertEquals(new Move(new Position(4, 1), new Position(4, 3)), aiMove.move());
    assertEquals(12, aiMove.score());
  }
}
