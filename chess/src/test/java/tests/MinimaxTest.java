package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
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
    game = Game.initialize(false, false, null, null, new HashMap<>());
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

  @Test
  public void testTimerDefault() {
    long timeLimit = 5;
    solver.setTime(timeLimit);
    solver.setDepth(5);

    long startTime = System.currentTimeMillis();
    solver.playAIMove(game);
    long endTime = System.currentTimeMillis();

    long elapsedTime = endTime - startTime;
    long remainingTime = solver.getTimer().getTimeRemaining();

    assertTrue(elapsedTime >= 0 && elapsedTime <= 5000 + 100);
    assertTrue(remainingTime <= 5000);
  }

  @Test
  public void testTimer2s() {
    long timeLimit = 2;
    solver.setTime(timeLimit);
    solver.setDepth(5);

    long startTime = System.currentTimeMillis();
    solver.playAIMove(game);
    long endTime = System.currentTimeMillis();

    long elapsedTime = endTime - startTime;
    long remainingTime = solver.getTimer().getTimeRemaining();

    assertTrue(elapsedTime >= 0 && elapsedTime <= 2000 + 100);
    assertTrue(remainingTime <= 5000);
  }
  /*
   @Test
   public void testTimerOverStartFunction() {
     long timeLimit = 1;
     solver.setDepth(20);
     solver.setTime(timeLimit);
     long startTime = System.currentTimeMillis();
     solver.playAIMove(game);
     long endTime = System.currentTimeMillis();

     long elapsedTime = endTime - startTime;
     long remainingTime = solver.getTimer().getTimeRemaining();

     assertTrue(elapsedTime >= 0 && elapsedTime <= timeLimit + 100);
     assertTrue(remainingTime <= timeLimit);
   }
  */
}
