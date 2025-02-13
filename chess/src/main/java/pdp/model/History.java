package pdp.model;

import java.util.Optional;
import java.util.Stack;
import java.util.logging.Logger;
import pdp.utils.Logging;

public class History {
  private static final Logger LOGGER = Logger.getLogger(History.class.getName());
  private HistoryNode currentMove;

  public History() {
    Logging.configureLogging(LOGGER);
    this.currentMove = null;
  }

  /**
   * Moves back to the previous move in history.
   *
   * @return the previous node, or an empty object if there is no previous move.
   */
  public Optional<HistoryNode> getPrevious() {
    if (currentMove == null || currentMove.previous == null) {
      return Optional.empty();
    }
    currentMove = currentMove.previous;
    return Optional.of(currentMove.previous);
  }

  /**
   * Adds a new move and his state to the history.
   *
   * @param state A HistoryState containing the move played in algebraic notation, the turn number,
   *     and the color played.
   */
  public void addMove(HistoryState state) {
    currentMove = new HistoryNode(state, currentMove);
  }

  /**
   * Returns the history of moves grouped by full turns in the format: "1. B h4-h5 W e3-e5"
   *
   * @return A string representing the history of moves.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    HistoryNode current = currentMove;
    Stack<HistoryNode> stack = new Stack<>();

    while (current != null) {
      stack.push(current);
      current = current.previous;
    }

    while (!stack.isEmpty()) {
      HistoryNode node = stack.pop();
      if (node != null) {
        sb.append(node.state.toString());
        if (!node.state.isWhite()) {
          sb.append("\n");
        }
      }
    }
    return sb.toString().trim();
  }
}
