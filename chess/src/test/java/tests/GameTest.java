package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.model.BitboardRepresentation;
import pdp.model.Game;
import pdp.model.Move;
import pdp.utils.Position;

public class GameTest {
  @Test
  public void playMoveTest() {

    // Correctly play move
    Game game = Game.initialize(false, false, null, null);
    BitboardRepresentation bitboards = new BitboardRepresentation();
    game.playMove(new Move(new Position(1, 0), new Position(2, 0)));
    bitboards.movePiece(new Position(1, 0), new Position(2, 0));
    assertEquals(game.getBoard().getBoard(), bitboards);

    // Play with wrong player
    // assertThrows()
    // game.playMove(new Move(new Position(2,0), new Position(3,0)));

    // Play move only in game
    game.playMove(new Move(new Position(6, 1), new Position(5, 1)));
    assertNotEquals(game.getBoard().getBoard(), bitboards);
    bitboards.movePiece(new Position(6, 1), new Position(5, 1)); // Play move in bitboards
    assertEquals(game.getBoard().getBoard(), bitboards);
  }
}
