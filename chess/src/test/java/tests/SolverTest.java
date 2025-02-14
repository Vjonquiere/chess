package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.Move;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.utils.Position;
import tests.helpers.MockBoard;

public class SolverTest {
  private Solver solver;

  @BeforeEach
  void setUp() {
    solver = new Solver();
  }

  @Test
  public void testEvaluationDumb() {
    Game game = Game.initialize(false, false, null, null);
    solver.setHeuristic(HeuristicType.DUMB);
    assertEquals(0, solver.evaluateBoard(game.getBoard(), true));

    game.playMove(new Move(new Position(1, 4), new Position(2, 4)));
    game.playMove(new Move(new Position(6, 3), new Position(5, 3)));
    game.playMove(new Move(new Position(0, 5), new Position(5, 0)));
    game.playMove(new Move(new Position(6, 2), new Position(5, 2)));
    game.playMove(new Move(new Position(5, 0), new Position(6, 1)));
    game.playMove(new Move(new Position(6, 4), new Position(5, 4)));
    game.playMove(new Move(new Position(6, 1), new Position(7, 2)));
    // white player has one more pawn and one more bishop than black player
    // position score for black
    assertEquals(-4, solver.evaluateBoard(game.getBoard(), false));
    // position score for white
    game.playMove(new Move(new Position(6, 0), new Position(5, 0)));
    assertEquals(4, solver.evaluateBoard(game.getBoard(), true));
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

    solver.setHeuristic(HeuristicType.DUMB);

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
    solver.setHeuristic(HeuristicType.DUMB);
    // same positions and rights --> will use the hash
    int score1 = solver.evaluateBoard(game.getBoard(), true);
    game.playMove(new Move(new Position(0, 1), new Position(2, 2)));
    game.playMove(new Move(new Position(7, 1), new Position(5, 0)));
    game.playMove(new Move(new Position(2, 2), new Position(0, 1)));
    game.playMove(new Move(new Position(5, 0), new Position(7, 1)));
    int score2 = solver.evaluateBoard(game.getBoard(), true);

    assertEquals(score1, score2);
  }
}
