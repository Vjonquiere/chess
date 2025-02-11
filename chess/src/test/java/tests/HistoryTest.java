package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.model.*;

public class HistoryTest {

  @Test
  void testHistoryToString() {
    // Arrange: Create a new history and add moves
    History history = new History();
    history.addMove(new HistoryState("e3-e5", 1, true)); // White move
    history.addMove(new HistoryState("h4-h5", 1, false)); // Black move
    history.addMove(new HistoryState("g1-f3", 2, true)); // White move
    history.addMove(new HistoryState("e7-e6", 2, false)); // Black move

    // Act: Convert history to string
    String historyOutput = history.toString();

    // Assert: Check expected output
    String expectedOutput = "1. W e3-e5 B h4-h5\n2. W g1-f3 B e7-e6";
    assertEquals(expectedOutput, historyOutput);
  }
}
