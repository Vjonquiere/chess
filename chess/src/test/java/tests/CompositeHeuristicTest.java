package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.ai.heuristics.*;
import pdp.model.board.Move;
import pdp.utils.Position;

public class CompositeHeuristicTest {
  Solver solver;
  Game game;

  @BeforeEach
  public void setup() {
    solver = new Solver();
    game = Game.initialize(false, false, null, null);
  }

  @Test
  public void addHeuristicTest() {
    ShannonBasic shannon = new ShannonBasic();
    List<Heuristic> heuristics = shannon.getHeuristics();
    assertEquals(3, heuristics.size(), "Expected exactly 3 heuristics");
    assertTrue(
        heuristics.stream().anyMatch(h -> h instanceof MobilityHeuristic),
        "Missing MobilityHeuristic");
    assertTrue(
        heuristics.stream().anyMatch(h -> h instanceof MaterialHeuristic),
        "Missing MaterialHeuristic");
    assertTrue(
        heuristics.stream().anyMatch(h -> h instanceof BadPawnsHeuristic),
        "Missing BadPawnsHeuristic");
  }

  @Test
  public void removeHeuristicTest() {
    ShannonBasic shannon = new ShannonBasic();
    List<Heuristic> heuristics = shannon.getHeuristics();
    assertEquals(3, heuristics.size(), "Expected exactly 3 heuristics");
    shannon.removeHeuristic(heuristics.get(0));
    assertEquals(2, heuristics.size(), "Expected exactly 2 heuristics");
    assertFalse(
        heuristics.stream().anyMatch(h -> h instanceof MobilityHeuristic),
        "MobilityHeuristic not removed");
  }

  @Test
  public void verifyCreationStandard() {
    StandardHeuristic standardHeuristic = new StandardHeuristic();
    List<Heuristic> heuristics = standardHeuristic.getHeuristics();
    assertEquals(7, heuristics.size(), "Expected exactly 7 heuristics");
    assertTrue(
        heuristics.stream().anyMatch(h -> h instanceof MobilityHeuristic),
        "Missing MobilityHeuristic");
    assertTrue(
        heuristics.stream().anyMatch(h -> h instanceof MaterialHeuristic),
        "Missing MaterialHeuristic");
    assertTrue(heuristics.stream().anyMatch(h -> h instanceof GameStatus), "Missing GameStatus");
    assertTrue(
        heuristics.stream().anyMatch(h -> h instanceof BadPawnsHeuristic),
        "Missing BadPawnsHeuristic");
    assertTrue(
        heuristics.stream().anyMatch(h -> h instanceof PawnChainHeuristic),
        "Missing PawnChainHeuristic");
    assertTrue(
        heuristics.stream().anyMatch(h -> h instanceof DevelopmentHeuristic),
        "Missing DevelopmentHeuristic");
    assertTrue(
        heuristics.stream().anyMatch(h -> h instanceof KingSafetyHeuristic),
        "Missing KingSafetyHeuristic");
  }

  @Test
  public void evaluateStandard() {
    solver.setHeuristic(HeuristicType.STANDARD);
    game.playMove(new Move(new Position(4, 1), new Position(4, 3)));
    game.playMove(new Move(new Position(4, 6), new Position(4, 4)));
    game.playMove(new Move(new Position(3, 0), new Position(7, 4)));
    game.playMove(new Move(new Position(1, 7), new Position(2, 5)));
    game.playMove(new Move(new Position(5, 0), new Position(2, 3)));
    game.playMove(new Move(new Position(6, 7), new Position(5, 5)));
    game.playMove(new Move(new Position(7, 4), new Position(5, 6)));

    int score = 0;
    Heuristic material = new MaterialHeuristic();
    Heuristic status = new GameStatus();
    Heuristic mobility = new MobilityHeuristic();
    Heuristic badPawnsHeuristic = new BadPawnsHeuristic();
    Heuristic pawnChainHeuristic = new PawnChainHeuristic();
    Heuristic developmentHeuristic = new DevelopmentHeuristic();
    Heuristic KingSafetyHeuristic = new KingSafetyHeuristic();
    score += material.evaluate(game.getBoard(), false);
    score += status.evaluate(game.getBoard(), false);
    score += mobility.evaluate(game.getBoard(), false);
    score += badPawnsHeuristic.evaluate(game.getBoard(), false);
    score += pawnChainHeuristic.evaluate(game.getBoard(), false);
    score += developmentHeuristic.evaluate(game.getBoard(), false);
    score += KingSafetyHeuristic.evaluate(game.getBoard(), false);
    assertEquals(score, solver.evaluateBoard(game.getBoard(), false));
  }
}
