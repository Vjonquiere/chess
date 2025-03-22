package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.ai.heuristics.*;
import pdp.model.board.Move;
import pdp.utils.Position;
import tests.helpers.MockBoard;

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
    assertEquals(-4, solver.evaluateBoard(game.getBoard(), false));
    // position score for white
    game.playMove(new Move(new Position(0, 6), new Position(0, 5)));
    assertEquals(4, solver.evaluateBoard(game.getBoard(), true));
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
    MockBoard board = new MockBoard();

    solver.setHeuristic(HeuristicType.MATERIAL);

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              solver.evaluateBoard(board, true);
            });
    assertEquals("Only available for bitboards", exception.getMessage());
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
  }
}
