package pdp.model;

import java.util.Optional;
import java.util.logging.Logger;
import pdp.utils.Logging;

public class History {
  private static final Logger LOGGER = Logger.getLogger(History.class.getName());
  private HistoryNode currentMove;

  public History() {
    Logging.configureLogging(LOGGER);
    this.currentMove = null;
  }

  public Optional<HistoryNode> getPrevious() {
    if (currentMove == null || currentMove.previous == null) {
      return Optional.empty();
    }
    currentMove = currentMove.previous;
    return Optional.of(currentMove.previous);
  }

  public void addMove(HistoryState state) {
    currentMove = new HistoryNode(state, currentMove);
  }
}
