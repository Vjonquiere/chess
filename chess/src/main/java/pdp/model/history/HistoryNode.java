package pdp.model.history;

import java.util.Optional;
import java.util.logging.Logger;
import pdp.utils.Logging;

/** Data structure used in History to represent a node. */
public class HistoryNode {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(HistoryNode.class.getName());

  /** State of the current node. */
  private final HistoryState state;

  /** Node leading to the current node. */
  private final HistoryNode previous;

  /** Node corresponding to the state reached after a move was played from this node. */
  private HistoryNode next;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Constructs a new HistoryNode with the specified state and previous node.
   *
   * @param state The HistoryState representing the state of the history at this node.
   * @param previous The previous HistoryNode in the history chain, or null if this is the first
   *     node.
   */
  public HistoryNode(final HistoryState state, final HistoryNode previous) {
    this.state = state;
    this.previous = previous;
    this.next = null;
  }

  /**
   * Moves back to the previous move in history.
   *
   * @return the previous node, or an empty object if there is no previous move.
   */
  public Optional<HistoryNode> getPrevious() {
    if (this.previous == null) {
      return Optional.empty();
    }
    return Optional.of(this.previous);
  }

  /**
   * Moves to the next move in history.
   *
   * @return the next node, or an empty object if there is no next move.
   */
  public Optional<HistoryNode> getNext() {
    if (this.next == null) {
      return Optional.empty();
    }
    return Optional.of(this.next);
  }

  /**
   * Sets the next node for the previous current move in the history. This is called when a new move
   * is added to the history.
   *
   * @param next representing the current move in the history.
   */
  public void setNext(final HistoryNode next) {
    this.next = next;
  }

  /**
   * Retrieves the state of the history at the current point.
   *
   * @return The HistoryState representing the current state.
   */
  public HistoryState getState() {
    return this.state;
  }
}
