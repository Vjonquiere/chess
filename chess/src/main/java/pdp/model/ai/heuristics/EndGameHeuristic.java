package pdp.model.ai.heuristics;

/**
 * EndGameHeuristic aggregates multiple heuristics to evaluate the board state during the endgame
 * phase of the match. It extends AbstractHeuristic to set up the Composite Design Pattern.
 */
public class EndGameHeuristic extends AbstractHeuristic {

  /** Creates a composite heuristic for the endgame phase of the game. */
  public EndGameHeuristic() {
    super();
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
