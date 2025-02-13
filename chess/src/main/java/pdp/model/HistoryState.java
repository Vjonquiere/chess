package pdp.model;

import java.util.logging.Logger;
import pdp.utils.Logging;

public class HistoryState {
  private static final Logger LOGGER = Logger.getLogger(HistoryState.class.getName());
  private final String stringMove;
  private final int fullTurn;
  private final boolean isWhite;

  /**
   * Constructs a history state with the given move notation.
   *
   * @param stringMove The notation of the move played.
   * @param fullTurn The number of the current full turn.
   * @param isWhite {true} if the move is white, {false} if it is black.
   */
  public HistoryState(String stringMove, int fullTurn, boolean isWhite) {
    Logging.configureLogging(LOGGER);
    this.stringMove = stringMove;
    this.fullTurn = fullTurn;
    this.isWhite = isWhite;
  }

  public String getStringMove() {
    return stringMove;
  }

  public int getFullTurn() {
    return fullTurn;
  }

  public boolean isWhite() {
    return isWhite;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    if (this.isWhite()) {
      sb.append(this.fullTurn).append(". W ").append(this.stringMove).append(" ");
    } else {
      sb.append("B ").append(this.stringMove);
    }

    return sb.toString();
  }
}
