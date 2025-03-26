package pdp.model.ai.heuristics;

import java.util.List;

/** Composite heuristic used in start and middle game. */
public class StandardHeuristic extends AbstractHeuristic {
  /**
   * StandardHeuristic aggregates multiple heuristics to evaluate the board state during the start
   * and middle game. It extends AbstractHeuristic to set up the Composite Design Pattern.
   */
  public StandardHeuristic() {
    super();
    super.addHeuristic(new WeightedHeuristic(new MaterialHeuristic(), 100));
    super.addHeuristic(new WeightedHeuristic(new MobilityHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new GameStatus(), 100));
    super.addHeuristic(new WeightedHeuristic(new BadPawnsHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new PawnChainHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new KingSafetyHeuristic(), 1));
    super.addHeuristic(new WeightedHeuristic(new DevelopmentHeuristic(), 3));
    // super.addHeuristic(new SpaceControlHeuristic());
  }

  /**
   * Composite heuristic with weights parametrized for the weight optimization.
   *
   * @param weight list of weights to add to the heuristics
   */
  public StandardHeuristic(final List<Float> weight) {
    super();
    super.addHeuristic(new WeightedHeuristic(new MaterialHeuristic(), weight.get(0)));
    super.addHeuristic(new WeightedHeuristic(new MobilityHeuristic(), weight.get(1)));
    super.addHeuristic(new WeightedHeuristic(new GameStatus(), weight.get(2)));
    super.addHeuristic(new WeightedHeuristic(new BadPawnsHeuristic(), weight.get(3)));
    super.addHeuristic(new WeightedHeuristic(new PawnChainHeuristic(), weight.get(4)));
    super.addHeuristic(new WeightedHeuristic(new KingSafetyHeuristic(), weight.get(5)));
    super.addHeuristic(new WeightedHeuristic(new DevelopmentHeuristic(), weight.get(6)));
    // super.addHeuristic(new SpaceControlHeuristic());
  }
}
