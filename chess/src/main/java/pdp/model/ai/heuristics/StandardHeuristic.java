package pdp.model.ai.heuristics;

public class StandardHeuristic extends AbstractHeuristic {
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
