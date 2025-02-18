package pdp.model.history;

import java.util.Optional;
import java.util.logging.Logger;
import pdp.utils.Logging;

public class HistoryNode {
  private static final Logger LOGGER = Logger.getLogger(HistoryNode.class.getName());
  HistoryState state;
  HistoryNode previous;

  public HistoryNode(HistoryState state, HistoryNode previous) {
    Logging.configureLogging(LOGGER);
    this.state = state;
    this.previous = previous;
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

  public HistoryState getState() {
    return this.state;
  }
}
