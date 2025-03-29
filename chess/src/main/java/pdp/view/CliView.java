package pdp.view;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.error;
import static pdp.utils.Logging.print;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Logger;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.CancelDrawCommand;
import pdp.controller.commands.CancelMoveCommand;
import pdp.controller.commands.PlayMoveCommand;
import pdp.controller.commands.ProposeDrawCommand;
import pdp.controller.commands.RestartCommand;
import pdp.controller.commands.RestoreMoveCommand;
import pdp.controller.commands.SaveGameCommand;
import pdp.controller.commands.StartGameCommand;
import pdp.controller.commands.SurrenderCommand;
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
import pdp.utils.TextGetter;
import pdp.utils.Timer;

/** View implementation to play in the terminal. */
public class CliView implements View {
  /** Boolean to indaicate whether the view is running or not. */
  private boolean running;

  /** Map of commands mapping a text to its description and action. */
  private final Map<String, CommandEntry> commands = new HashMap<>();

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(CliView.class.getName());

  /** Build a new Cli view (initialize all commands). */
  public CliView() {
    commands.put(
        "move", new CommandEntry(this::moveCommand, TextGetter.getText("moveHelpDescription")));
    commands.put(
        "draw", new CommandEntry(this::drawCommand, TextGetter.getText("drawHelpDescription")));
    commands.put(
        "undraw",
        new CommandEntry(this::undrawCommand, TextGetter.getText("undrawHelpDescription")));
    commands.put(
        "help", new CommandEntry(this::helpCommand, TextGetter.getText("helpHelpDescription")));
    commands.put(
        "quit", new CommandEntry(this::quitCommand, TextGetter.getText("quitHelpDescription")));
    commands.put(
        "history",
        new CommandEntry(this::historyCommand, TextGetter.getText("historyHelpDescription")));
    commands.put(
        "board",
        new CommandEntry(this::displayBoardCommand, TextGetter.getText("boardHelpDescription")));
    commands.put(
        "save", new CommandEntry(this::saveCommand, TextGetter.getText("saveHelpDescription")));
    commands.put(
        "undo", new CommandEntry(this::undoCommand, TextGetter.getText("undoHelpDescription")));
    commands.put(
        "redo", new CommandEntry(this::redoCommand, TextGetter.getText("redoHelpDescription")));
    commands.put(
        "surrender",
        new CommandEntry(this::surrenderCommand, TextGetter.getText("surrenderHelpDescription")));
    commands.put(
        "time", new CommandEntry(this::timeCommand, TextGetter.getText("timeHelpDescription")));
    commands.put(
        "restart",
        new CommandEntry(this::restartCommand, TextGetter.getText("restartHelpDescription")));
  }

  /**
   * Starts the CLI view, allowing the user to input commands.
   *
   * @return The thread that was started.
   */
  @Override
  public Thread start() {
    running = true;
    BagOfCommands.getInstance().addCommand(new StartGameCommand());
    return startUserInputListener();
  }

  /**
   * Called when a game event occurs.
   *
   * @param event The type of event that occurred.
   */
  @Override
  public void onGameEvent(final EventType event) {
    Game.getInstance().getViewLock().lock();
    try {
      switch (event) {
        case GAME_STARTED:
          print(TextGetter.getText("welcomeCLI"));
          print(TextGetter.getText("welcomeInstructions"));
          print(Game.getInstance().getGameRepresentation());
          break;
        case MOVE_PLAYED:
          print(Game.getInstance().getGameRepresentation());
          break;
        case WIN_WHITE:
          print(TextGetter.getText("whiteWin"));
          break;
        case WIN_BLACK:
          print(TextGetter.getText("blackWin"));
          break;
        case DRAW:
          print(TextGetter.getText("onDraw"));
          break;
        case WHITE_DRAW_PROPOSAL:
          print(TextGetter.getText("drawProposal", TextGetter.getText("white")));
          break;
        case BLACK_DRAW_PROPOSAL:
          print(TextGetter.getText("drawProposal", TextGetter.getText("black")));
          break;
        case WHITE_UNDRAW:
          print(TextGetter.getText("cancelDrawProposal", TextGetter.getText("white")));
          break;
        case BLACK_UNDRAW:
          print(TextGetter.getText("cancelDrawProposal", TextGetter.getText("black")));
          break;
        case DRAW_ACCEPTED:
          print(TextGetter.getText("drawAccepted"));
          break;
        case GAME_SAVED:
          print(TextGetter.getText("gameSaved"));
          break;
        case MOVE_UNDO:
          print(TextGetter.getText("moveUndone"));
          print(Game.getInstance().getGameRepresentation());
          break;
        case WHITE_UNDO_PROPOSAL:
          print(TextGetter.getText("undoProposal", TextGetter.getText("white")));
          print(TextGetter.getText("undoInstructions", TextGetter.getText("black")));
          break;
        case BLACK_UNDO_PROPOSAL:
          print(TextGetter.getText("undoProposal", TextGetter.getText("black")));
          print(TextGetter.getText("undoInstructions", TextGetter.getText("white")));
          break;
        case MOVE_REDO:
          print(TextGetter.getText("moveRedone"));
          print(Game.getInstance().getGameRepresentation());
          break;
        case WHITE_REDO_PROPOSAL:
          print(TextGetter.getText("redoProposal", TextGetter.getText("white")));
          print(TextGetter.getText("redoInstructions", TextGetter.getText("black")));
          break;
        case BLACK_REDO_PROPOSAL:
          print(TextGetter.getText("redoProposal", TextGetter.getText("black")));
          print(TextGetter.getText("redoInstructions", TextGetter.getText("white")));
          break;
        case OUT_OF_TIME_WHITE:
          print(TextGetter.getText("outOfTime", TextGetter.getText("white")));
          break;
        case OUT_OF_TIME_BLACK:
          print(TextGetter.getText("outOfTime", TextGetter.getText("black")));
          break;
        case THREEFOLD_REPETITION:
          print(TextGetter.getText("threeFoldRepetition"));
          break;
        case INSUFFICIENT_MATERIAL:
          print(TextGetter.getText("insufficientMaterial"));
          break;
        case FIFTY_MOVE_RULE:
          print(TextGetter.getText("fiftyMoveRule"));
          break;
        case WHITE_RESIGNS:
          print(TextGetter.getText("resigns", TextGetter.getText("white")));
          break;
        case BLACK_RESIGNS:
          print(TextGetter.getText("resigns", TextGetter.getText("black")));
          break;
        case CHECKMATE_WHITE:
          print(
              TextGetter.getText(
                  "checkmate", TextGetter.getText("white"), TextGetter.getText("black")));
          break;
        case CHECKMATE_BLACK:
          print(
              TextGetter.getText(
                  "checkmate", TextGetter.getText("black"), TextGetter.getText("white")));
          break;
        case STALEMATE:
          print(TextGetter.getText("stalemate"));
          break;
        case AI_PLAYING:
          print(TextGetter.getText("ai_playing"));
          break;
        case GAME_RESTART:
          print(TextGetter.getText("gameRestart"));
          print(TextGetter.getText("welcomeCLI"));
          print(TextGetter.getText("welcomeInstructions"));
          print(Game.getInstance().getGameRepresentation());
          break;
        default:
          debug(LOGGER, "Received unknown game event: " + event);
          break;
      }
      Game.getInstance().getWorkingViewCondition().signal();
    } finally {
      Game.getInstance().getViewLock().unlock();
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
      error(String.valueOf(exception));
      running = false;
    }
  }

  /**
   * Starts a new thread that listens for user input from the console.
   *
   * @return The thread that listens for user input.
   */
  private Thread startUserInputListener() {
    final Thread inputThread =
        new Thread(
            () -> {
              final Scanner scanner = new Scanner(System.in);
              while (running) {
                final String input = scanner.nextLine();
                handleUserInput(input);
              }
              scanner.close();
            });

    inputThread.setDaemon(true);
    inputThread.start();

    return inputThread;
  }

  /**
   * Handles user input from the console.
   *
   * @param input The user's input.
   */
  private void handleUserInput(String input) {
    input = input.trim().toLowerCase();
    final String[] parts = input.split(" ", 2);

    final CommandEntry commandEntry = commands.get(parts[0]);

    if (commandEntry != null) {
      final Consumer<String> command = commands.get(parts[0]).action();
      command.accept(parts.length > 1 ? parts[1] : "");
    } else {
      print(TextGetter.getText("unknownCommand", input));
      this.helpCommand("");
    }
  }

  /**
   * Displays the current state of the board and the next player.
   *
   * @param args Unused argument
   */
  private void displayBoardCommand(final String args) {
    print(Game.getInstance().getGameRepresentation());
  }

  /**
   * Handles the history command by displaying the history of moves made in the game.
   *
   * @param args Unused argument
   */
  private void historyCommand(final String args) {
    print(TextGetter.getText("historyTitle"));
    print(Game.getInstance().getHistory().toString());
  }

  /**
   * Handles the move command.
   *
   * @param args The move in standard text notation.
   */
  private void moveCommand(final String args) {
    BagOfCommands.getInstance().addCommand(new PlayMoveCommand(args));
  }

  /**
   * Handles the help command.
   *
   * @param args Unused argument
   */
  private void helpCommand(final String args) {
    print(TextGetter.getText("availableCommandsTitle"));
    for (final Map.Entry<String, CommandEntry> entry : commands.entrySet()) {
      print(String.format("  %-10s - %s%n", entry.getKey(), entry.getValue().description()));
    }
  }

  /**
   * Handles the save command.
   *
   * @param args The path to where the game should be saved.
   */
  private void saveCommand(final String args) {
    BagOfCommands.getInstance().addCommand(new SaveGameCommand(args.strip()));
  }

  /**
   * Handles the draw command.
   *
   * @param args Unused argument
   */
  private void drawCommand(final String args) {
    BagOfCommands.getInstance()
        .addCommand(new ProposeDrawCommand(Game.getInstance().getGameState().isWhiteTurn()));
  }

  /**
   * Handles the undraw command.
   *
   * @param args Unused argument
   */
  private void undrawCommand(final String args) {
    BagOfCommands.getInstance()
        .addCommand(new CancelDrawCommand(Game.getInstance().getGameState().isWhiteTurn()));
  }

  /**
   * Handles the quit command.
   *
   * @param args Unused argument
   */
  private void quitCommand(final String args) {
    print(TextGetter.getText("quitting"));
    this.running = false;
  }

  /**
   * Handles the undo command by reverting the last move in history.
   *
   * @param args Unused argument
   */
  private void undoCommand(final String args) {
    BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
  }

  /**
   * Handles the redo command by re-executing a previously undone move.
   *
   * @param args Unused argument
   */
  private void redoCommand(final String args) {
    BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
  }

  /**
   * Handles the restart command by restarting a new game.
   *
   * @param args Unused argument
   */
  private void restartCommand(final String args) {
    BagOfCommands.getInstance().addCommand(new RestartCommand());
  }

  /**
   * Handles the surrender command.
   *
   * @param args Unused argument
   */
  private void surrenderCommand(final String args) {
    BagOfCommands.getInstance()
        .addCommand(new SurrenderCommand(Game.getInstance().getGameState().isWhiteTurn()));
  }

  /**
   * Handles the time command. Displays the remaining time in the blitz timer if the mode is on.
   *
   * @param args Unused argument
   */
  private void timeCommand(final String args) {
    final Timer timer =
        Game.getInstance().getTimer(Game.getInstance().getGameState().isWhiteTurn());
    if (timer != null) {
      print(TextGetter.getText("timeRemainingCurrent", timer.getTimeRemainingString()));
    } else {
      print(TextGetter.getText("noTimer"));
    }
  }

  private record CommandEntry(Consumer<String> action, String description) {}
}
