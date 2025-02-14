package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.Move;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.utils.Position;

public class SolverTest {

  @Test
  public void testEvaluationDumb() {
    Solver solver = new Solver();
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
    // position score for white
    assertEquals(4, solver.evaluateBoard(game.getBoard(), true));
    // position score for black
    assertEquals(-4, solver.evaluateBoard(game.getBoard(), false));
  }
}
