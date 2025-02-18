package pdp.model.ai.heuristics;

/**
 * EndGameHeuristic aggregates multiple heuristics to evaluate the board state during the endgame
 * phase of the match. It extends AbstractHeuristic to setup the Composite Design Pattern.
 */
public class EndGameHeuristic extends AbstractHeuristic {

  public EndGameHeuristic() {
    addHeuristic(new KingActivityHeuristic());
    addHeuristic(new PromotionHeuristic());
    addHeuristic(new BishopEndgameHeuristic());
    addHeuristic(new MaterialHeuristic());
    addHeuristic(new BadPawnsHeuristic());
    addHeuristic(new OpponentCheck());
    addHeuristic(new KingSafetyHeuristic());
  }
}
