package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;

public class HeuristicTests {
  Game game;
  Solver solver;

  @BeforeEach
  public void setup() {}

  @Test
  public void BasicMaterialTest() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.MATERIAL);
    assertEquals(0, solver.evaluateBoard(game.getBoard(), true));
    assertEquals(0, solver.evaluateBoard(game.getBoard(), false));
  }

  @Test
  public void BasicMobilityTest() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.MOBILITY);
    assertEquals(0, solver.evaluateBoard(game.getBoard(), true));
    assertEquals(0, solver.evaluateBoard(game.getBoard(), false));
  }

  @Test
  public void testEndGameHeuristic() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    int score = -35;
    assertEquals(score, solver.evaluateBoard(game.getBoard(), true));
    assertEquals(score, solver.evaluateBoard(game.getBoard(), false));
  }
}
