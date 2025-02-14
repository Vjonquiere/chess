package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.utils.Position;

public class HistoryTest {

  @Test
  void testHistoryToString() {
    // Arrange: Create a new history and add moves
    History history = new History();
    history.addMove(new HistoryState(Move.fromString("e3-e5"), 1, true)); // White move
    history.addMove(new HistoryState(Move.fromString("h4-h5"), 1, false)); // Black move
    history.addMove(new HistoryState(Move.fromString("g1-f3"), 2, true)); // White move
    history.addMove(new HistoryState(Move.fromString("e7-e6"), 2, false)); // Black move

    // Act: Convert history to string
    String historyOutput = history.toString();

    // Assert: Check expected output
    String expectedOutput = "1. W e3-e5 B h4-h5\n2. W g1-f3 B e7-e6";
    assertEquals(expectedOutput, historyOutput);
  }

  @Test
  void HistoryTestInGame() {
    Game game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(1, 4), new Position(3, 4)); // Pion avance de deux cases
    game.playMove(move);
    Move move2 = new Move(new Position(6, 4), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(1, 3), new Position(2, 3)); // Pion avance d'une case
    game.playMove(move3);
    Move move4 = new Move(new Position(6, 3), new Position(5, 3));
    game.playMove(move4);

    String historyOutput = game.getHistory().toString();

    // Assert: Check expected output
    String expectedOutput = "1. W e2-e4 B e7-e5\n2. W d2-d3 B d7-d6";
    assertEquals(expectedOutput, historyOutput);
  }
}
