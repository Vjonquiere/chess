package pdp.model;

import java.util.logging.Logger;
import pdp.utils.Logging;

public class HistoryState {
  private static final Logger LOGGER = Logger.getLogger(HistoryState.class.getName());
  Board state;
  Move previousMove;

  public HistoryState(Board state, Move previousMove) {
    Logging.configureLogging(LOGGER);
    this.state = state;
    this.previousMove = previousMove;
  }

  public Board getState() {
    return state;
  }

  public Move getPreviousMove() {
    return previousMove;
  }
}
