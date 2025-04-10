package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pdp.model.ai.AiMove;
import pdp.model.board.Move;
import pdp.utils.Position;

public class AiMoveTest {

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  public void aiMoveTest() {
    AiMove aiMove = new AiMove(new Move(new Position(4, 1), new Position(4, 3)), 12);

    assertEquals(new Move(new Position(4, 1), new Position(4, 3)), aiMove.move());
    assertEquals(12, aiMove.score());
  }
}
