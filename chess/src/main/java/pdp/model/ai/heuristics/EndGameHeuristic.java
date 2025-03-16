package pdp.model.ai.heuristics;

/**
 * EndGameHeuristic aggregates multiple heuristics to evaluate the board state during the endgame
 * phase of the match. It extends AbstractHeuristic to setup the Composite Design Pattern.
 */
public class EndGameHeuristic extends AbstractHeuristic {

  public EndGameHeuristic() {
    addHeuristic(new WeightedHeuristic(new KingActivityHeuristic(), 1));
    addHeuristic(new WeightedHeuristic(new PromotionHeuristic(), 5));
    addHeuristic(new WeightedHeuristic(new BishopEndgameHeuristic(), 1));
    addHeuristic(new WeightedHeuristic(new MaterialHeuristic(), 50));
    addHeuristic(new WeightedHeuristic(new BadPawnsHeuristic(), 1));
    addHeuristic(new WeightedHeuristic(new GameStatus(), 100));
    addHeuristic(new WeightedHeuristic(new KingSafetyHeuristic(), 1));
    addHeuristic(new WeightedHeuristic(new PawnChainHeuristic(), 1));
    addHeuristic(new WeightedHeuristic(new KingOppositionHeuristic(), 1));
  }
}
