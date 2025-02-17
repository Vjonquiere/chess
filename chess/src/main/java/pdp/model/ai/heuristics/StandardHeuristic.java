package pdp.model.ai.heuristics;

public class StandardHeuristic extends AbstractHeuristic {
  public StandardHeuristic() {
    super.addHeuristic(new MaterialHeuristic());
    super.addHeuristic(new MobilityHeuristic());
    super.addHeuristic(new OpponentCheck());
  }
}
