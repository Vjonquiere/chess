package pdp.model.ai.heuristics;

import java.util.ArrayList;
import java.util.List;
import pdp.model.board.BoardRepresentation;

/**
 * Used for design pattern composite : all heuristic composed of several simple heuristics must
 * extend this class.
 */
public abstract class AbstractHeuristic implements Heuristic {
  /** List of heuristics for composite heuristics. */
  private final List<WeightedHeuristic> heuristics = new ArrayList<>();

  /**
   * Adds a heuristic to the composite heuristic.
   *
   * @param heuristic Heuristic to be added
   */
  public void addHeuristic(final WeightedHeuristic heuristic) {
    heuristics.add(heuristic);
  }

  /**
   * Removes a heuristic to the composite heuristic.
   *
   * @param heuristic Heuristic to be removed
   */
  public void removeHeuristic(final WeightedHeuristic heuristic) {
    heuristics.remove(heuristic);
  }

  /**
   * Retries the list of heuristics (without their weights) composing this heuristic.
   *
   * @return list of heuristics
   */
  public List<Heuristic> getHeuristics() {
    final List<Heuristic> heuristicList = new ArrayList<>();
    for (final WeightedHeuristic heuristic : heuristics) {
      heuristicList.add(heuristic.heuristic());
    }
    return heuristicList;
  }

  /**
   * Retries the list of weighted heuristics composing this heuristic.
   *
   * @return list of weighted heuristics
   */
  public List<WeightedHeuristic> getWeightedHeuristics() {
    return heuristics;
  }

  /**
   * Evaluates the board state by summing up the scores of all sub-heuristics.
   *
   * @param board The current Board.
   * @param isWhite true if the player is white, false if he is black
   * @return Total score of all the heuristics evaluation
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    int score = 0;
    for (final WeightedHeuristic heuristic : heuristics) {
      score += heuristic.heuristic().evaluate(board, isWhite) * heuristic.weight();
    }
    return score;
  }
}
