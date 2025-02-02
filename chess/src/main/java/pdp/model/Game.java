package pdp.model;

import java.util.List;
import pdp.events.Subject;
import pdp.model.ai.Solver;

public class Game extends Subject {
  private static Game instance;
  private Timer timer;
  private boolean isTimed;
  private Board board;
  private History history;
  private Solver solver;
  private boolean isWhiteAI;
  private boolean isBlackAI;

  private Game(
      boolean isWhiteAI,
      boolean isBlackAI,
      Solver solver,
      boolean isTimed,
      Timer timer,
      History history) {
    this.isWhiteAI = isWhiteAI;
    this.isBlackAI = isBlackAI;
    this.solver = solver;
    this.isTimed = isTimed;
    this.timer = timer;
    this.history = history;
  }

  public static Game initialize(
      boolean isWhiteAI,
      boolean isBlackAI,
      Solver solver,
      boolean isTimed,
      Timer timer,
      History history) {
    instance = new Game(isWhiteAI, isBlackAI, solver, isTimed, timer, history);
    return instance;
  }

  @Override
  public void notifyObservers() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public boolean playMove(Move move) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public List<Move> getMovesHistory() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public String getStringHistory() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public void resetGame() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public boolean isOver() {
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
