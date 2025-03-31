package pdp.model.history;

import java.util.logging.Logger;
import pdp.model.GameState;
import pdp.model.board.Move;
import pdp.utils.Logging;

/** Data structure use in History node to represent a game state. */
public class HistoryState {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(HistoryState.class.getName());

  /** GameState of the node. */
  private final GameState gameState;

  /** Move made to get to this game state. */
  private final Move move;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Constructs a history state with the given move notation.
   *
   * @param move The move played.
   * @param gameState a copy of the gameState after the move played.
   */
  public HistoryState(Move move, GameState gameState) {
    this.move = move;
    this.gameState = gameState;
  }

  /**
   * Retrieves the move associated with this object.
   *
   * @return The Move object representing the move.
   */
  public Move getMove() {
    return this.move;
  }

  /**
   * Retrieves the full turn number from the game state.
   *
   * @return The turn number as an integer.
   */
  public int getFullTurn() {
    return this.gameState.getFullTurn();
  }

  /**
   * Determines if the current player is white.
   *
   * @return true if the current player is white, false otherwise.
   */
  public boolean isWhite() {
    return this.gameState.isWhiteTurn();
  }

  /**
   * Retrieves the game state associated with this object.
   *
   * @return The GameState object representing the current state of the game.
   */
  public GameState getGameState() {
    return this.gameState;
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
    if (!(this.move.getSource().x() == -1)) {
      if (!this.isWhite()) {
        sb.append(this.gameState.getFullTurn())
            .append(". W ")
            .append(this.move.toAlgebraicString())
            .append(" ");
      } else {
        sb.append("B ").append(this.move);
      }
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
    if (!(this.move.getSource().x() == -1)) {
      if (!this.isWhite()) {
        sb.append(this.gameState.getFullTurn()).append(". W ").append(this.move).append(" ");
      } else {
        sb.append("B ").append(this.move);
      }
    }

    return sb.toString();
  }
}
