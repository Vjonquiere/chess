package pdp.model.ai.heuristics;

public class StandardHeuristic extends AbstractHeuristic {
  /**
   * StandardHeuristic aggregates multiple heuristics to evaluate the board state during the start
   * and middle game. It extends AbstractHeuristic to set up the Composite Design Pattern.
   */
  public StandardHeuristic() {
    super.addHeuristic(new WeightedHeuristic(new MaterialHeuristic(), 10));
    super.addHeuristic(new WeightedHeuristic(new MobilityHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new GameStatus(), 1));
    super.addHeuristic(new WeightedHeuristic(new BadPawnsHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new PawnChainHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new KingSafetyHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new DevelopmentHeuristic(), 1));
    // super.addHeuristic(new SpaceControlHeuristic());
  }
}
