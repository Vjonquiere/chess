package pdp.model;

import java.util.List;
import java.util.logging.Logger;
import pdp.events.Subject;
import pdp.exceptions.IllegalMoveException;
import pdp.model.ai.Solver;
import pdp.utils.Logging;

public class Game extends Subject {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static Game instance;
  private GameState gameState;
  private boolean isTimed;
  private Solver solver;
  private boolean isWhiteAI;
  private boolean isBlackAI;

  private Game(
      boolean isWhiteAI, boolean isBlackAI, Solver solver, boolean isTimed, GameState gameState) {
        Logging.configureLogging(LOGGER);
    this.isWhiteAI = isWhiteAI;
    this.isBlackAI = isBlackAI;
    this.solver = solver;
    this.isTimed = isTimed;
    this.gameState = gameState;
  }

  /**
   * Creates a new instance of the Game class and stores it in the instance variable.
   *
   * @param isWhiteAI Whether the white player is an AI.
   * @param isBlackAI Whether the black player is an AI.
   * @param solver The solver to be used for AI moves.
   * @param isTimed Whether there is a time limit for the game.
   * @param gameState Contains the board, history, current player, timers if blitz mode is on
   * @return The newly created instance of Game.
   */
  public static Game initialize(
      boolean isWhiteAI, boolean isBlackAI, Solver solver, boolean isTimed, GameState gameState) {
    instance = new Game(isWhiteAI, isBlackAI, solver, isTimed, gameState);
    return instance;
  }

  /**
   * Tries to play the given move on the game.
   *
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is not legal.
   */
  public void playMove(Move move) throws IllegalMoveException {
    // TODO
    // Timer resets when a move is played
    // Switch turn when move is played (through GameState)
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public List<Move> getMovesHistory() {
>>>>>>> chess/src/main/java/pdp/model/Game.java
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

    Timer timer = gameState.getMoveTimer();
    if (timer != null) {
      timer.timeRemaining();
    }

    sb.append(gameState.getBoard().getAsciiRepresentation());

    sb.append("\n\n");

    sb.append("To play: ");
    sb.append(gameState.isWhiteTurn() ? "White" : "Black");

    return sb.toString();
  }

  public static Game getInstance() {
    // TODO
    throw new UnsupportedOperationException("Method not implemented");
  }
}
