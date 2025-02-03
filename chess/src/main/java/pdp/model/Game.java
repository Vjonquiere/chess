package pdp.model;

import java.util.List;
import java.util.logging.Logger;
import pdp.events.Subject;
import pdp.model.ai.Solver;
import pdp.utils.Logging;

public class Game extends Subject {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  Game instance;
  Timer timer;
  boolean isTimed;
  Board board;
  History history;
  Solver solver;

  private Game() {
    Logging.configureLogging(LOGGER);
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public void notifyObservers() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  List<Move> getMovesHistory() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  String getStringHistory() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  void resetGame() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  boolean isOver() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public String getGameRepresentation() {
    StringBuilder sb = new StringBuilder();

    if (this.isTimed) {
      sb.append("Played with time remaining: ");
      sb.append(this.timer.timeRemainingString());
      sb.append("\n\n");
    }

    sb.append(this.board.getAsciiRepresentation());

    sb.append("\n\n");

    sb.append("To play: ");
    sb.append(this.board.isWhite ? "White" : "Black");

    return sb.toString();
  }

  public static Game getInstance() {
    // TODO
    throw new UnsupportedOperationException("Method not implemented");
  }
}
