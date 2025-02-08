package pdp.model.ai;

public class Solver {

  AlgorithmType algorithm;
  HeuristicType heuristic;
  int depth;
  int time;

  public Solver() {}

  public void setAlgorithm(AlgorithmType algorithm) {
    this.algorithm = algorithm;
  }

  public void setHeuristic(HeuristicType heuristic) {
    this.heuristic = heuristic;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public void setTime(int time) {
    this.time = time;
  }
}
