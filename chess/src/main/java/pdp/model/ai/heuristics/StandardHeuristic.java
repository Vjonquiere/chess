package pdp.model.ai.heuristics;

public class StandardHeuristic extends AbstractHeuristic {
  private boolean threefoldImpact = true;

  /**
   * StandardHeuristic aggregates multiple heuristics to evaluate the board state during the start
   * and middle game. It extends AbstractHeuristic to set up the Composite Design Pattern.
   */
  public StandardHeuristic() {
    super.addHeuristic(new WeightedHeuristic(new MaterialHeuristic(), 100));
    super.addHeuristic(new WeightedHeuristic(new MobilityHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new GameStatus(), 100));
    super.addHeuristic(new WeightedHeuristic(new BadPawnsHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new PawnChainHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new KingSafetyHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new DevelopmentHeuristic(), 3));
    // super.addHeuristic(new SpaceControlHeuristic());
  }

  public StandardHeuristic(boolean isThreefoldImpact) {
    this();
    this.threefoldImpact = isThreefoldImpact;
  }

  @Override
  public boolean isThreefoldImpact() {
    return this.threefoldImpact;
  }
}
