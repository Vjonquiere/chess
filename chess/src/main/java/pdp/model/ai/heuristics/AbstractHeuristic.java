package pdp.model.ai.heuristics;

import java.util.ArrayList;
import java.util.List;
import pdp.model.board.Board;

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
   * @param board The current Board.
   * @param isWhite true if the player is white, false if he is black
   * @return
   */
  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    for (Heuristic heuristic : heuristics) {
      score += heuristic.evaluate(board, isWhite);
    }
    return score;
  }
}
