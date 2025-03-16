package pdp.model.ai.heuristics;

import java.util.ArrayList;
import java.util.List;
import pdp.model.board.Board;

/**
 * Used for design pattern composite : all heuristic composed of several simple heuristics must
 * extend this class.
 */
public abstract class AbstractHeuristic implements Heuristic {
  List<WeightedHeuristic> heuristics = new ArrayList<>();

  /**
   * Adds a heuristic to the composite heuristic.
   *
   * @param heuristic Heuristic to be added
   */
  public void addHeuristic(WeightedHeuristic heuristic) {
    heuristics.add(heuristic);
  }

  /**
   * Removes a heuristic to the composite heuristic.
   *
   * @param heuristic Heuristic to be removed
   */
  public void removeHeuristic(WeightedHeuristic heuristic) {
    heuristics.remove(heuristic);
  }

  public List<Heuristic> getHeuristics() {
    List<Heuristic> h = new ArrayList<>();
    for (WeightedHeuristic heuristic : heuristics) {
      h.add(heuristic.heuristic());
    }
    return h;
  }

  public List<WeightedHeuristic> getWeightedHeuristics() {
    return heuristics;
  }

  /**
   * Evaluates the board state by summing up the scores of all sub-heuristics.
   *
   * @param board The current Board.
   * @param isWhite true if the player is white, false if he is black
   * @return
   */
  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    for (WeightedHeuristic heuristic : heuristics) {
      score += heuristic.heuristic().evaluate(board, isWhite) * heuristic.weight();
    }
    return score;
  }
}
