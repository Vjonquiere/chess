package pdp.model.history;

import java.util.logging.Logger;
import pdp.model.board.Move;
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

  /**
   * Converts the history state into a string representing the move in algebraic notation.
   *
   * <p>The format of the string is:
   *
   * <ul>
   *   <li>For white moves: "1. W e2-e4 "
   *   <li>For black moves: "B Qe7xe5+"
   * </ul>
   *
   * @return A string representing the move in algebraic notation.
   */
  public String toAlgebraicString() {
    StringBuilder sb = new StringBuilder();

    if (this.isWhite()) {
      sb.append(this.fullTurn).append(". W ").append(this.move.toAlgebraicString()).append(" ");
    } else {
      sb.append("B ").append(this.move.toString());
    }

    return sb.toString();
  }

  /**
   * Converts the history state into a string representing the move in a format that shows the full
   * turn number and the move in standard algebraic notation.
   *
   * <p>The format of the string is:
   *
   * <ul>
   *   <li>For white moves: "1. W e2-e4 "
   *   <li>For black moves: "B e7-e5"
   * </ul>
   *
   * @return A string representing the move in the given format.
   */
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
