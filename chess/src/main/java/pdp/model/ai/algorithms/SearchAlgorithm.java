package pdp.model.ai.algorithms;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import pdp.model.Game;
import pdp.model.ai.AiMove;

/** Common interface for all AI algorithms to be able to change the solver's algorithm with ease. */
public abstract class SearchAlgorithm {
  private final AtomicLong visitedNodes = new AtomicLong(0);
  private final List<Long> visitedNodeList = new CopyOnWriteArrayList<>();

  /**
   * Determines the best move using the implemented algorithm.
   *
   * @param game The current game state.
   * @param depth The number of moves to look ahead.
   * @param player The current player (true for white, false for black).
   * @return The best move for the player.
   */
  public abstract AiMove findBestMove(Game game, int depth, boolean player);

  public void addNode() {
    visitedNodes.incrementAndGet();
  }

  public void clearNode() {
    visitedNodeList.add(visitedNodes.get());
    visitedNodes.set(0);
  }

  public long getMean() {
    if (visitedNodeList.isEmpty()) return 0;

    long totalNodes = 0;
    for (long nodeCount : visitedNodeList) {
      totalNodes += nodeCount;
    }
    return totalNodes / visitedNodeList.size();
  }

  public List<Long> getVisitedNodeList() {
    return visitedNodeList;
  }

  public long getVisitedNodes() {
    return visitedNodes.get();
  }

  public long getLastVisitedNodeCount() {
    return visitedNodeList.get(visitedNodeList.size() - 1);
  }
}
