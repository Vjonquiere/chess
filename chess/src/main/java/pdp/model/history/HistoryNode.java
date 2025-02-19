package pdp.model.history;

import java.util.Optional;
import java.util.logging.Logger;
import pdp.utils.Logging;

public class HistoryNode {
  private static final Logger LOGGER = Logger.getLogger(HistoryNode.class.getName());
  HistoryState state;
  HistoryNode previous;
  HistoryNode next;

  public HistoryNode(HistoryState state, HistoryNode previous) {
    Logging.configureLogging(LOGGER);
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
  public void setNext(HistoryNode next) {
    this.next = next;
  }

  public HistoryState getState() {
    return this.state;
  }
}
