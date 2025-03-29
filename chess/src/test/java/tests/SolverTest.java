package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.ai.heuristics.*;
import pdp.model.board.Move;
import pdp.utils.Position;
import tests.helpers.DummyBoardRepresentation;

public class SolverTest {
  private Solver solver;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @AfterEach
  void tearDownConsole() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
  }

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
    solver = new Solver();
  }

  @Test
  public void testEvaluationMaterial() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver.setHeuristic(HeuristicType.MATERIAL);
    assertEquals(0, solver.evaluateBoard(game.getBoard(), true));

    game.playMove(new Move(new Position(4, 1), new Position(4, 2)));
    game.playMove(new Move(new Position(3, 6), new Position(3, 5)));
    game.playMove(new Move(new Position(5, 0), new Position(0, 5)));
    game.playMove(new Move(new Position(2, 6), new Position(2, 5)));
    game.playMove(new Move(new Position(0, 5), new Position(1, 6)));
    game.playMove(new Move(new Position(4, 6), new Position(4, 5)));
    game.playMove(new Move(new Position(1, 6), new Position(2, 7)));
    // white player has one more pawn and one more bishop than black player
    // position score for black
    float expected = 4 * (100f / 103);
    assertEquals(-expected, solver.evaluateBoard(game.getBoard(), false)); // rounded value
    // position score for white
    game.playMove(new Move(new Position(0, 6), new Position(0, 5)));
    assertEquals(expected, solver.evaluateBoard(game.getBoard(), true)); // rounded value
  }

  @Test
  public void testEvaluationErrors() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              solver.evaluateBoard(null, true);
            });
    assertEquals("Board is null", exception.getMessage());
  }

  @Test
  public void testEvaluationErrorBoardNonBitboardRepresentation() {
    DummyBoardRepresentation board = new DummyBoardRepresentation();

    solver.setHeuristic(HeuristicType.MATERIAL);

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              solver.evaluateBoard(board, true);
            });
    assertEquals("Only available for bitboards.", exception.getMessage());
  }

  @Test
  public void testEvaluationHash() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver.setHeuristic(HeuristicType.MATERIAL);
    // same positions and rights --> will use the hash
    float score1 = solver.evaluateBoard(game.getBoard(), true);
    game.playMove(new Move(new Position(1, 0), new Position(2, 2)));
    game.playMove(new Move(new Position(1, 7), new Position(0, 5)));
    game.playMove(new Move(new Position(2, 2), new Position(1, 0)));
    game.playMove(new Move(new Position(0, 5), new Position(1, 7)));
    float score2 = solver.evaluateBoard(game.getBoard(), true);

    assertEquals(score1, score2);
  }

  @Test
  public void testSetDepthClassic() {
    assertEquals(4, solver.getDepth());
    solver.setDepth(7);
    assertEquals(7, solver.getDepth());
  }

  @Test
  public void testSetDepthError() {
    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              solver.setDepth(0);
            });
    assertEquals("Depth must be greater than 0", exception.getMessage());

    Exception exception2 =
        assertThrows(
            RuntimeException.class,
            () -> {
              solver.setDepth(-2);
            });
    assertEquals("Depth must be greater than 0", exception2.getMessage());
  }

  @Test
  public void testSetTimeError() {
    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              solver.setTime(0);
            });
    assertEquals("Time must be greater than 0", exception.getMessage());

    Exception exception2 =
        assertThrows(
            RuntimeException.class,
            () -> {
              solver.setTime(-2);
            });
    assertEquals("Time must be greater than 0", exception2.getMessage());
  }

  /*
  @Test
  public void testNotEnoughTime() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver.setTime(1);
    solver.setDepth(10000);
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    solver.playAIMove(game);

    assertTrue(game.getGameState().hasBlackResigned());
  }
  */

  @Test
  public void testSetHeuristic() {
    solver.setHeuristic(HeuristicType.KING_SAFETY);
    assertInstanceOf(KingSafetyHeuristic.class, solver.getHeuristic());
    solver.setHeuristic(HeuristicType.PAWN_CHAIN);
    assertInstanceOf(PawnChainHeuristic.class, solver.getHeuristic());
    solver.setHeuristic(HeuristicType.SHANNON);
    assertInstanceOf(ShannonBasic.class, solver.getHeuristic());
    solver.setHeuristic(HeuristicType.KING_ACTIVITY);
    assertInstanceOf(KingActivityHeuristic.class, solver.getHeuristic());
    solver.setHeuristic(HeuristicType.BISHOP_ENDGAME);
    assertInstanceOf(BishopEndgameHeuristic.class, solver.getHeuristic());
    solver.setHeuristic(HeuristicType.KING_OPPOSITION);
    assertInstanceOf(KingOppositionHeuristic.class, solver.getHeuristic());
    solver.setHeuristic(HeuristicType.STANDARD);
    assertInstanceOf(StandardHeuristic.class, solver.getHeuristic());
    solver.setHeuristic(HeuristicType.STANDARD_LIGHT);
    assertInstanceOf(StandardLightHeuristic.class, solver.getHeuristic());
  }

  @Test
  public void testGetBestMove() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    game.playMove(new Move(new Position(4, 1), new Position(4, 3))); // white pawn
    game.playMove(new Move(new Position(3, 6), new Position(3, 4))); // black pawn
    game.playMove(new Move(new Position(6, 0), new Position(5, 2))); // white knight

    solver.setHeuristic(HeuristicType.MATERIAL);
    Move materialBestMove = solver.getBestMove(game);
    assertNotNull(materialBestMove);

    solver.setHeuristic(HeuristicType.KING_SAFETY);
    Move kingSafetyBestMove = solver.getBestMove(game);
    assertNotNull(kingSafetyBestMove);

    solver.setDepth(3);
    Move bestMoveAtDepth3 = solver.getBestMove(game);
    assertNotNull(bestMoveAtDepth3);
  }

  @Test
  public void testGetBestMoveWithTimer() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    solver.setTime(5);

    Move bestMove = solver.getBestMove(game);
    assertNotNull(bestMove);

    assertNotNull(solver.getTimer());
  }

  @Test
  public void testSetHeuristicWithWeights() {
    List<Float> weights = Arrays.asList(0.5f, 0.3f, 0.2f, 0.5f, 0.3f, 0.2f, 0.1f);

    solver.setHeuristic(HeuristicType.STANDARD, weights);
    assertInstanceOf(StandardHeuristic.class, solver.getHeuristic());
    assertEquals(HeuristicType.STANDARD, solver.getCurrentHeuristic());

    List<HeuristicType> heuristicsToTest =
        Arrays.asList(
            HeuristicType.MATERIAL,
            HeuristicType.KING_SAFETY,
            HeuristicType.SPACE_CONTROL,
            HeuristicType.DEVELOPMENT,
            HeuristicType.PAWN_CHAIN,
            HeuristicType.MOBILITY,
            HeuristicType.BAD_PAWNS,
            HeuristicType.SHANNON,
            HeuristicType.GAME_STATUS,
            HeuristicType.KING_ACTIVITY,
            HeuristicType.BISHOP_ENDGAME,
            HeuristicType.KING_OPPOSITION,
            HeuristicType.ENDGAME);

    for (HeuristicType heuristic : heuristicsToTest) {
      solver.setHeuristic(heuristic, weights);

      switch (heuristic) {
        case MATERIAL -> assertInstanceOf(MaterialHeuristic.class, solver.getHeuristic());
        case KING_SAFETY -> assertInstanceOf(KingSafetyHeuristic.class, solver.getHeuristic());
        case SPACE_CONTROL -> assertInstanceOf(SpaceControlHeuristic.class, solver.getHeuristic());
        case DEVELOPMENT -> assertInstanceOf(DevelopmentHeuristic.class, solver.getHeuristic());
        case PAWN_CHAIN -> assertInstanceOf(PawnChainHeuristic.class, solver.getHeuristic());
        case MOBILITY -> assertInstanceOf(MobilityHeuristic.class, solver.getHeuristic());
        case BAD_PAWNS -> assertInstanceOf(BadPawnsHeuristic.class, solver.getHeuristic());
        case SHANNON -> assertInstanceOf(ShannonBasic.class, solver.getHeuristic());
        case GAME_STATUS -> assertInstanceOf(GameStatus.class, solver.getHeuristic());
        case KING_ACTIVITY -> assertInstanceOf(KingActivityHeuristic.class, solver.getHeuristic());
        case BISHOP_ENDGAME ->
            assertInstanceOf(BishopEndgameHeuristic.class, solver.getHeuristic());
        case KING_OPPOSITION ->
            assertInstanceOf(KingOppositionHeuristic.class, solver.getHeuristic());
        case ENDGAME -> assertInstanceOf(EndGameHeuristic.class, solver.getHeuristic());
        default -> throw new IllegalArgumentException("Unexpected value: " + heuristic);
      }

      assertEquals(heuristic, solver.getCurrentHeuristic());
    }
  }
}
