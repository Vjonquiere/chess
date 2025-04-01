package pdp.model.ai.algorithms;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import pdp.model.Game;
import pdp.model.ai.AiMove;

/** Common interface for all AI algorithms to be able to change the solver's algorithm with ease. */
public abstract class SearchAlgorithm {
  /** Number of visited nodes. */
  private final AtomicLong visitedNodes = new AtomicLong(0);

  /** List containing the number of visited nodes per launch of the search algorithm. */
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

  /** Adds a node to the field visitedNodes. */
  public void addNode() {
    visitedNodes.incrementAndGet();
  }

  /**
   * Adds the number of visited nodes during this run of the algorithm to the visited nodes list.
   * Clears the number of visited nodes.
   */
  public void clearNode() {
    visitedNodeList.add(visitedNodes.get());
    visitedNodes.set(0);
  }

  /**
   * Retrieves the mean of the visited nodes list.
   *
   * @return mean of visitedNodesList
   */
  public long getMean() {
    if (visitedNodeList.isEmpty()) {
      return 0;
    }

    long totalNodes = 0;
    for (final long nodeCount : visitedNodeList) {
      totalNodes += nodeCount;
    }
    return totalNodes / visitedNodeList.size();
  }

  /**
   * Retrieves the number of visited nodes during this run of the algorithm.
   *
   * @return field visitedNodes
   */
  public long getVisitedNodes() {
    return visitedNodes.get();
  }
}
