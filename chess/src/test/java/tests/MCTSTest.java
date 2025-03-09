package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.ai.AlgorithmType;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Position;

public class MCTSTest {
  Solver solver;
  Game game;

  @BeforeEach
  void setUp() {
    solver = new Solver();
    solver.setAlgorithm(AlgorithmType.MCTS);
    game = Game.initialize(false, false, null, null, new HashMap<>());
  }

  private void printBoard(Game game) {
    char[][] board = game.getBoard().getAsciiRepresentation();
    for (char[] row : board) {
      System.out.println(new String(row));
    }
    System.out.println();
  }

  @Test
  public void testMctsScholarsMate() {
    // Simulate Scholar's mate
    game.playMove(new Move(new Position(4, 1), new Position(4, 3)));
    game.playMove(new Move(new Position(4, 6), new Position(4, 4)));
    game.playMove(new Move(new Position(3, 0), new Position(7, 4)));
    game.playMove(new Move(new Position(1, 7), new Position(2, 5)));
    game.playMove(new Move(new Position(5, 0), new Position(2, 3)));
    game.playMove(new Move(new Position(6, 7), new Position(5, 5)));

    solver.playAIMove(game);
    System.out.println("After AI move:");
    printBoard(game);

    assertTrue(game.getGameState().isGameOver());
  }
}
