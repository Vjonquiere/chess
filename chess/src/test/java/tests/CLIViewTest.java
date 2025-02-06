package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import pdp.model.Game;

public class CLIViewTest {
  @Test
  public void testBoardToASCII() {
    Game game = Game.getInstance();

    String expected =
        "rnbqkbnr"
            + "\npppppppp"
            + "\n________"
            + "\n________"
            + "\n________"
            + "\n________"
            + "\nPPPPPPPP"
            + "\nRNBQKBNR";

    assertEquals(expected, game.getBoard().getAsciiRepresentation());
  }
}
