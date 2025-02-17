package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import pdp.model.ai.heuristics.*;

public class CompositeHeuristicTest {

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
}
