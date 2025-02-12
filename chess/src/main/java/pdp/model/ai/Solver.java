package pdp.model.ai;

public class Solver {

  AlgorithmType algorithm;
  HeuristicType heuristic;
  int depth;
  int time;

  public Solver() {}

  /**
   * Set the algorithm to be used.
   *
   * @param algorithm The algorithm to use.
   */
  public void setAlgorithm(AlgorithmType algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Set the heuristic to be used.
   *
   * @param heuristic The heuristic to use.
   */
  public void setHeuristic(HeuristicType heuristic) {
    this.heuristic = heuristic;
  }

  /**
   * Set the maximum depth the solver should explore.
   *
   * @param depth The depth to use.
   */
  public void setDepth(int depth) {
    this.depth = depth;
  }

  /**
   * Set the maximum time (in milliseconds) the solver should spend computing a move.
   *
   * @param time The time to use.
   */
  public void setTime(int time) {
    this.time = time;
  }
}
