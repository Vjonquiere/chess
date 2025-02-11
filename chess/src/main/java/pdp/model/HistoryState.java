package pdp.model;

import java.util.logging.Logger;
import pdp.utils.Logging;

public class HistoryState {
  private static final Logger LOGGER = Logger.getLogger(HistoryState.class.getName());
  private final String stringMove;
  private final int turnNumber;
  private final boolean isWhite;

  public HistoryState(String stringMove, int turnNumber, boolean isWhite) {
    Logging.configureLogging(LOGGER);
    this.stringMove = stringMove;
    this.turnNumber = turnNumber;
    this.isWhite = isWhite;
  }

  /* public String getMove() {
    return move;
  } */
}
