package pdp.view;

import static pdp.utils.Logging.error;
import static pdp.utils.Logging.print;

import java.util.Objects;
import java.util.logging.Logger;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.StartGameCommand;
import pdp.events.EventType;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.exceptions.FailedRedoException;
import pdp.exceptions.FailedSaveException;
import pdp.exceptions.FailedUndoException;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPositionException;
import pdp.exceptions.InvalidPromoteFormatException;
import pdp.exceptions.MoveParsingException;
import pdp.model.Game;
import pdp.model.GameAbstract;
import pdp.model.GameState;
import pdp.utils.Logging;

/** View used to handle the game in contest mode. */
public class ContestView implements View {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(ContestView.class.getName());

  /** Boolean to indicate if the view is currently running. */
  private boolean running;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Initializes the view by settings the commands and parametrizing the game with the correct fifty
   * move rule and threefold repetition. The other chess engine use a 5-fold repetition and a 75
   * move rule, we adapt our game the same way.
   */
  public ContestView() {
    GameAbstract.setThreeFoldLimit(3);
    GameState.setFiftyMoveLimit(50);
  }

  /**
   * Starts the CLI view, allowing the user to input commands.
   *
   * @return The thread that was started.
   */
  @Override
  public Thread start() {
    running = true;
    final Thread contestThread =
        new Thread(
            () -> {
              BagOfCommands.getInstance().addCommand(new StartGameCommand());

              while (running) {
                try {
                  Thread.sleep(100);
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                }
              }
            });

    contestThread.start();

    return contestThread;
  }

  /**
   * Called when a game event occurs.
   *
   * @param event The type of event that occurred.
   */
  @Override
  public void onGameEvent(final EventType event) {
    if (Objects.requireNonNull(event) == EventType.MOVE_PLAYED) {
      print(Game.getInstance().getGameRepresentation());
      running = false;
    }
  }

  /**
   * Prints the error message for an exception related to user input.
   *
   * <p>Stops the game if the exception is not related to handled.
   *
   * @param exception The exception that was thrown.
   */
  @Override
  public void onErrorEvent(final Exception exception) {
    if (exception instanceof IllegalMoveException
        || exception instanceof MoveParsingException
        || exception instanceof InvalidPositionException
        || exception instanceof FailedSaveException
        || exception instanceof InvalidPromoteFormatException
        || exception instanceof CommandNotAvailableNowException
        || exception instanceof FailedUndoException
        || exception instanceof FailedRedoException) {
      error(exception.getMessage());
    } else {
      print(Game.getInstance().getGameRepresentation());
      error(String.valueOf(exception));
    }
  }
}
