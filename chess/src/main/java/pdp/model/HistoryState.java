package pdp.model;

import java.util.logging.Logger;
import pdp.utils.Logging;

public class HistoryState {
  private static final Logger LOGGER = Logger.getLogger(HistoryState.class.getName());
  private final int fullTurn;
  private final boolean isWhite;
  private final Move move;

  /**
   * Constructs a history state with the given move notation.
   *
   * @param move The move played.
   * @param fullTurn The number of the current full turn.
   * @param isWhite {true} if the move is white, {false} if it is black.
   */
  public HistoryState(Move move, int fullTurn, boolean isWhite) {
    Logging.configureLogging(LOGGER);
    this.move = move;
    this.fullTurn = fullTurn;
    this.isWhite = isWhite;
  }

  public Move getMove() {
    return this.move;
  }

  public int getFullTurn() {
    return fullTurn;
  }

  public boolean isWhite() {
    return isWhite;
  }

  public String toAlgebricString() {
    StringBuilder sb = new StringBuilder();

    if (this.isWhite()) {
      sb.append(this.fullTurn).append(". W ").append(this.move.toAlgebricString()).append(" ");
    } else {
      sb.append("B ").append(this.move.toString());
    }

    return sb.toString();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    if (this.isWhite()) {
      sb.append(this.fullTurn).append(". W ").append(this.move.toString()).append(" ");
    } else {
      sb.append("B ").append(this.move.toString());
    }

    return sb.toString();
  }
}
