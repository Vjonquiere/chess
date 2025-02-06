package tests;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import pdp.model.Game;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CLIViewTest {
  @Test
  public void testBoardToASCII() {
    Game game = Game.getInstance();

    char[][] expectedBoard = {
      {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
      {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
      {'_', '_', '_', '_', '_', '_', '_', '_'},
      {'_', '_', '_', '_', '_', '_', '_', '_'},
      {'_', '_', '_', '_', '_', '_', '_', '_'},
      {'_', '_', '_', '_', '_', '_', '_', '_'},
      {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
      {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    };

    assertTrue(Arrays.deepEquals(expectedBoard, game.getBoard().getAsciiRepresentation()));
  }
}
