package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pdp.model.Game;
import pdp.model.ai.AlgorithmType;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Position;

public class AlphaBetaTest {
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
  }

  @BeforeEach
  void setUp() {
    Game.initialize(false, false, null, null, null, new HashMap<>());
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
    solver = new Solver();
    solver.setAlgorithm(AlgorithmType.ALPHA_BETA);
    solver.setHeuristic(HeuristicType.STANDARD);
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
  }

  static Stream<AlgorithmType> algorithmProvider() {
    return Stream.of(
        AlgorithmType.ALPHA_BETA,
        AlgorithmType.ALPHA_BETA_PARALLEL,
        AlgorithmType.ALPHA_BETA_ID,
        AlgorithmType.ALPHA_BETA_ID_PARALLEL);
  }

  @ParameterizedTest
  @MethodSource("algorithmProvider")
  public void easyCase(AlgorithmType algorithm) {
    solver.setAlgorithm(algorithm);

    // Simulate Scholar's mate
    game.playMove(new Move(new Position(4, 1), new Position(4, 3)));
    game.playMove(new Move(new Position(4, 6), new Position(4, 4)));
    game.playMove(new Move(new Position(3, 0), new Position(7, 4)));
    game.playMove(new Move(new Position(1, 7), new Position(2, 5)));
    game.playMove(new Move(new Position(5, 0), new Position(2, 3)));
    game.playMove(new Move(new Position(6, 7), new Position(5, 5)));

    solver.playAiMove(game);
    assertTrue(game.getGameState().isGameOver());
  }

  @ParameterizedTest
  @MethodSource("algorithmProvider")
  public void testTimerDefault(AlgorithmType algorithm) {
    solver.setAlgorithm(algorithm);
    solver.setTime(5);
    solver.setDepth(5);

    long startTime = System.currentTimeMillis();
    solver.playAiMove(game);
    long endTime = System.currentTimeMillis();

    long elapsedTime = endTime - startTime;
    long remainingTime = solver.getTimer().getTimeRemaining();

    assertTrue(elapsedTime >= 0 && elapsedTime <= solver.getTime() * 1000 + 500);
    assertTrue(remainingTime <= solver.getTime() * 1000);
  }

  @ParameterizedTest
  @MethodSource("algorithmProvider")
  public void testTimer2s(AlgorithmType algorithm) {
    solver.setAlgorithm(algorithm);
    long timeLimit = 2;
    solver.setTime(timeLimit);
    solver.setDepth(5);

    long startTime = System.currentTimeMillis();
    solver.playAiMove(game);
    long endTime = System.currentTimeMillis();

    long elapsedTime = endTime - startTime;
    long remainingTime = solver.getTimer().getTimeRemaining();
    timeLimit *= 1000;

    assertTrue(elapsedTime >= 0 && elapsedTime <= timeLimit + 500);
    assertTrue(remainingTime <= timeLimit);
  }
}
