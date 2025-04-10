package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static pdp.utils.Logging.configureGlobalLogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @AfterEach
  void tearDownConsole() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
    configureGlobalLogger();
  }

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
    configureGlobalLogger();
    solver = new Solver();
    solver.setAlgorithm(AlgorithmType.MINIMAX);
    solver.setHeuristic(HeuristicType.STANDARD);
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
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
    solver.playAiMove(game);
    assertTrue(game.getGameState().isGameOver());
  }

  @Test
  public void testTimerDefault() {
    long timeLimit = 5;
    solver.setTime(timeLimit);
    solver.setDepth(5);

    long startTime = System.currentTimeMillis();
    solver.playAiMove(game);
    long endTime = System.currentTimeMillis();

    long elapsedTime = endTime - startTime;
    long remainingTime = solver.getTimer().getTimeRemaining();

    assertTrue(elapsedTime >= 0 && elapsedTime <= 5000 + 500);
    assertTrue(remainingTime <= 5000);
  }

  @Test
  public void testTimer2s() {
    long timeLimit = 2;
    solver.setTime(timeLimit);
    solver.setDepth(5);

    long startTime = System.currentTimeMillis();
    solver.playAiMove(game);
    long endTime = System.currentTimeMillis();

    long elapsedTime = endTime - startTime;
    long remainingTime = solver.getTimer().getTimeRemaining();

    assertTrue(elapsedTime >= 0 && elapsedTime <= 2000 + 500);
    assertTrue(remainingTime <= 5000);
  }
}
