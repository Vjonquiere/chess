package pdp.model.history;

import static pdp.utils.Logging.debug;

import java.util.Optional;
import java.util.Stack;
import java.util.logging.Logger;
import pdp.utils.Logging;

/** Data structure to represent a history. */
public class History {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(History.class.getName());

  /** Move made to get to this history state. */
  private HistoryNode currentMove;

  static {
    Logging.configureLogging(LOGGER);
  }

  /** Constructs a new History instance. Initializes logging and sets the current move to null. */
  public History() {
    this.currentMove = null;
  }

  /** Clears the current move from the history. */
  public void clear() {
    this.currentMove = null;
  }

  /**
   * Retrieves the current move node in the history.
   *
   * @return An Optional containing the current HistoryNode if it is present, or an empty Optional
   *     if there is no current move.
   */
  public Optional<HistoryNode> getCurrentMove() {
    if (this.currentMove == null) {
      return Optional.empty();
    }
    return Optional.of(this.currentMove);
  }

  /**
   * Updates the currentMove node to the specified HistoryNode. This method is called when
   * navigating through the history and applying a history node to the game.
   *
   * @param currentMove the HistoryNode representing the current move in the history.
   */
  public void setCurrentMove(final HistoryNode currentMove) {
    this.currentMove = currentMove;
  }

  /**
   * Adds a new move and his state to the history.
   *
   * @param state A HistoryState containing the move played in algebraic notation, the turn number,
   *     and the color played.
   */
  public void addMove(final HistoryState state) {
    debug(LOGGER, "Adding new state to History");
    debug(LOGGER, state.getMove().toString());
    debug(LOGGER, state.isWhite() + " " + state.getFullTurn());

    this.currentMove = new HistoryNode(state, this.currentMove);
    if (this.currentMove.getPrevious().isPresent()) {
      this.currentMove.getPrevious().ifPresent(prev -> prev.setNext(this.currentMove));
    }
  }

  /**
   * Returns the history of moves grouped by full turns in the format: "1. W h4xh5 B Qe3-e5" with
   * english algebraic notation.
   *
   * @return A string representing the history of moves.
   */
  public String toAlgebraicString() {
    final StringBuilder builder = new StringBuilder();
    HistoryNode current = currentMove;
    final Stack<HistoryNode> stack = new Stack<>();

    while (current != null) {
      stack.push(current);
      current = current.getPrevious().orElse(null); // Utilisation de getPrevious()
    }

    while (!stack.isEmpty()) {
      final HistoryNode node = stack.pop();
      builder.append(node.getState().toString());

      if (node.getState().isWhite()) {
        builder.append('\n');
      }
    }

    return builder.toString().trim();
  }

  /**
   * Returns the history of moves grouped by full turns in the format: "1. W h4-h5 B e3-e5"
   *
   * @return A string representing the history of moves.
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    HistoryNode current = currentMove;
    final Stack<HistoryNode> stack = new Stack<>();

    while (current != null) {
      stack.push(current);
      current = current.getPrevious().orElse(null); // Utilisation du getter
    }

    while (!stack.isEmpty()) {
      final HistoryNode node = stack.pop();
      builder.append(node.getState().toString());

      if (node.getState().isWhite()) {
        builder.append('\n');
      }
    }

    return builder.toString().trim();
  }
}
