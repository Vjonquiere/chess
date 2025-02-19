package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.ai.AlgorithmType;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Position;

public class MinimaxTest {
  Solver solver;
  Game game;

  @BeforeEach
  void setUp() {
    solver = new Solver();
    solver.setAlgorithm(AlgorithmType.MINIMAX);
    solver.setHeuristic(HeuristicType.STANDARD);
    game = Game.initialize(false, false, null, null);
  }

  @Test
  public void easyCase() {
    // Simulate Scholar's mate
    game.playMove(new Move(new Position(4, 1), new Position(4, 3)));
    game.playMove(new Move(new Position(4, 6), new Position(4, 4)));
    game.playMove(new Move(new Position(3, 0), new Position(7, 4)));
    game.playMove(new Move(new Position(1, 7), new Position(2, 5)));
    game.playMove(new Move(new Position(5, 0), new Position(2, 3)));
    game.playMove(new Move(new Position(6, 7), new Position(5, 5)));
    solver.setDepth(2);
    solver.playAIMove(game);
    assertTrue(game.getGameState().isGameOver());
  }
}
