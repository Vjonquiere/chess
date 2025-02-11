package pdp.model;

import java.util.logging.Logger;
import pdp.utils.Logging;

class HistoryNode {
  private static final Logger LOGGER = Logger.getLogger(History.class.getName());
  HistoryState state;
  HistoryNode previous;

  public HistoryNode(HistoryState state, HistoryNode previous) {
    Logging.configureLogging(LOGGER);
    this.state = state;
    this.previous = previous;
  }
}
