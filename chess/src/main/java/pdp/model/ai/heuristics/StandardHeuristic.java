package pdp.model.ai.heuristics;

public class StandardHeuristic extends AbstractHeuristic {
  /**
   * StandardHeuristic aggregates multiple heuristics to evaluate the board state during the start
   * and middle game. It extends AbstractHeuristic to set up the Composite Design Pattern.
   */
  public StandardHeuristic() {
    super.addHeuristic(new MaterialHeuristic());
    super.addHeuristic(new MobilityHeuristic());
    super.addHeuristic(new GameStatus());
    super.addHeuristic(new BadPawnsHeuristic());
    super.addHeuristic(new PawnChainHeuristic());
    super.addHeuristic(new KingSafetyHeuristic());
    super.addHeuristic(new DevelopmentHeuristic());
    // super.addHeuristic(new SpaceControlHeuristic());
  }
}
