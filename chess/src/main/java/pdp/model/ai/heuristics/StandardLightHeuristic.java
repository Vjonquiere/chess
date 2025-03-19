package pdp.model.ai.heuristics;

public class StandardLightHeuristic extends AbstractHeuristic {
  /**
   * StandardHeuristic aggregates multiple heuristics to evaluate the board state during the start
   * and middle game. It extends AbstractHeuristic to set up the Composite Design Pattern.
   */
  public StandardLightHeuristic() {
    super.addHeuristic(new WeightedHeuristic(new MaterialHeuristic(), 100));
    super.addHeuristic(new WeightedHeuristic(new GameStatus(), 100));
    super.addHeuristic(new WeightedHeuristic(new DevelopmentHeuristic(), 3));
  }
}
