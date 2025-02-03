package pdp.model;

import java.util.Optional;
import java.util.Stack;
import java.util.logging.Logger;
import pdp.utils.Logging;

public class History {
  private static final Logger LOGGER = Logger.getLogger(History.class.getName());
  Stack<HistoryState> histStack;
  Stack<HistoryState> revertStack;

  public History() {
    Logging.configureLogging(LOGGER);
    histStack = new Stack<>();
    revertStack = new Stack<>();
  }

  public Optional<HistoryState> getPrevious() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public Optional<HistoryState> getNext() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public boolean addMove(HistoryState state) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
