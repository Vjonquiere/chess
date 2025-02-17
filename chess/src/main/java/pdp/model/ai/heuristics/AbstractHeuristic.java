package pdp.model.ai.heuristics;

import java.util.ArrayList;
import java.util.List;
import pdp.model.Game;

/**
 * Used for design pattern composite : all heuristic composed of several simple heuristics must
 * extend this class.
 */
public abstract class AbstractHeuristic implements Heuristic {
  List<Heuristic> heuristics = new ArrayList<>();

  /**
   * Adds a heuristic to the composite heuristic.
   *
   * @param heuristic Heuristic to be added
   */
  public void addHeuristic(Heuristic heuristic) {
    heuristics.add(heuristic);
  }

  /**
   * Removes a heuristic to the composite heuristic.
   *
   * @param heuristic Heuristic to be removed
   */
  public void removeHeuristic(Heuristic heuristic) {
    heuristics.remove(heuristic);
  }

  public List<Heuristic> getHeuristics() {
    return heuristics;
  }

  /**
   * Evaluates the board state by summing up the scores of all sub-heuristics.
   *
   * @param game The current Board.
   * @param isWhite true if the player is white, false if he is black
   * @return
   */
  @Override
  public int evaluate(Game game, boolean isWhite) {
    int score = 0;
    for (Heuristic heuristic : heuristics) {
      score += heuristic.evaluate(game, isWhite);
    }
    return score;
  }
}
