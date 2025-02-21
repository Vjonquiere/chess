package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Position;
import tests.helpers.MockBoard;

public class SolverTest {
  private Solver solver;

  @BeforeEach
  void setUp() {
    solver = new Solver();
  }

  @Test
  public void testEvaluationMaterial() {
    Game game = Game.initialize(false, false, null, null);
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
    assertEquals(-7, solver.evaluateBoard(game.getBoard(), false));
    // position score for white
    game.playMove(new Move(new Position(0, 6), new Position(0, 5)));
    assertEquals(7, solver.evaluateBoard(game.getBoard(), true));
  }

  @Test
  public void testEvaluationErrors() {
    Game game = Game.initialize(false, false, null, null);

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
    Game game = Game.initialize(false, false, null, null);
    solver.setHeuristic(HeuristicType.MATERIAL);
    // same positions and rights --> will use the hash
    int score1 = solver.evaluateBoard(game.getBoard(), true);
    game.playMove(new Move(new Position(1, 0), new Position(2, 2)));
    game.playMove(new Move(new Position(1, 7), new Position(0, 5)));
    game.playMove(new Move(new Position(2, 2), new Position(1, 0)));
    game.playMove(new Move(new Position(0, 5), new Position(1, 7)));
    int score2 = solver.evaluateBoard(game.getBoard(), true);

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
}
