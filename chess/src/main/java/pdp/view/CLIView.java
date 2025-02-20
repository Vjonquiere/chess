package pdp.view;

import static pdp.utils.Logging.DEBUG;

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
import pdp.controller.commands.RestoreMoveCommand;
import pdp.controller.commands.SaveGameCommand;
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

public class CLIView implements View {
  private boolean running = false;
  private final Map<String, CommandEntry> commands = new HashMap<>();
  private static final Logger LOGGER = Logger.getLogger(CLIView.class.getName());

  public CLIView() {
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
  }

  /**
   * Starts the CLI view, allowing the user to input commands.
   *
   * @return The thread that was started.
   */
  @Override
  public Thread start() {
    running = true;
    return startUserInputListener();
  }

  /**
   * Called when a game event occurs.
   *
   * @param event The type of event that occurred.
   */
  @Override
  public void onGameEvent(EventType event) {
    switch (event) {
      case GAME_STARTED:
        System.out.println(TextGetter.getText("welcomeCLI"));
        System.out.println(TextGetter.getText("welcomeInstructions"));
        System.out.println(Game.getInstance().getGameRepresentation());
        break;
      case MOVE_PLAYED:
        System.out.println(Game.getInstance().getGameRepresentation());
        break;
      case WIN_WHITE:
        System.out.println(TextGetter.getText("whiteWin"));
        break;
      case WIN_BLACK:
        System.out.println(TextGetter.getText("blackWin"));
        break;
      case DRAW:
        System.out.println(TextGetter.getText("onDraw"));
        break;
      case WHITE_DRAW_PROPOSAL:
        System.out.println(TextGetter.getText("drawProposal", TextGetter.getText("white")));
        break;
      case BLACK_DRAW_PROPOSAL:
        System.out.println(TextGetter.getText("drawProposal", TextGetter.getText("black")));
        break;
      case WHITE_UNDRAW:
        System.out.println(TextGetter.getText("cancelDrawProposal", TextGetter.getText("white")));
        break;
      case BLACK_UNDRAW:
        System.out.println(TextGetter.getText("cancelDrawProposal", TextGetter.getText("black")));
        break;
      case DRAW_ACCEPTED:
        System.out.println(TextGetter.getText("drawAccepted"));
        break;
      case GAME_SAVED:
        System.out.println(TextGetter.getText("gameSaved"));
        break;
      case MOVE_UNDO:
        System.out.println(TextGetter.getText("moveUndone"));
        System.out.println(Game.getInstance().getGameRepresentation());
        break;
      case MOVE_REDO:
        System.out.println(TextGetter.getText("moveRedone"));
        System.out.println(Game.getInstance().getGameRepresentation());
        break;
      case OUT_OF_TIME_WHITE:
        System.out.println(TextGetter.getText("outOfTime", TextGetter.getText("white")));
        break;
      case OUT_OF_TIME_BLACK:
        System.out.println(TextGetter.getText("outOfTime", TextGetter.getText("black")));
        break;
      case THREE_FOLD_REPETITION:
        System.out.println(TextGetter.getText("threeFoldRepetition"));
        break;
      case INSUFFICIENT_MATERIAL:
        System.out.println(TextGetter.getText("insufficientMaterial"));
        break;
      case FIFTY_MOVE_RULE:
        System.out.println(TextGetter.getText("fiftyMoveRule"));
        break;
      case WHITE_RESIGNS:
        System.out.println(TextGetter.getText("resigns", TextGetter.getText("white")));
        break;
      case BLACK_RESIGNS:
        System.out.println(TextGetter.getText("resigns", TextGetter.getText("black")));
        break;
      case CHECKMATE_WHITE:
        System.out.println(
            TextGetter.getText(
                "checkmate", TextGetter.getText("white"), TextGetter.getText("black")));
        break;
      case CHECKMATE_BLACK:
        System.out.println(
            TextGetter.getText(
                "checkmate", TextGetter.getText("black"), TextGetter.getText("white")));
        break;
      case STALEMATE:
        System.out.println(TextGetter.getText("stalemate"));
        break;
      default:
        DEBUG(LOGGER, "Received unknown game event: " + event);
        break;
    }
  }

  /**
   * Prints the error message for an exception related to user input.
   *
   * <p>Stops the game if the exception is not related to handled.
   *
   * @param e The exception that was thrown.
   */
  @Override
  public void onErrorEvent(Exception e) {
    if (e instanceof IllegalMoveException
        || e instanceof MoveParsingException
        || e instanceof InvalidPositionException
        || e instanceof FailedSaveException
        || e instanceof InvalidPromoteFormatException
        || e instanceof CommandNotAvailableNowException
        || e instanceof FailedUndoException
        || e instanceof FailedRedoException) {
      System.out.println(e.getMessage());
    } else {
      System.err.println(e);
      e.printStackTrace();
      running = false;
    }
  }

  /**
   * Starts a new thread that listens for user input from the console.
   *
   * @return The thread that listens for user input.
   */
  private Thread startUserInputListener() {
    Thread inputThread =
        new Thread(
            () -> {
              Scanner scanner = new Scanner(System.in);
              while (running) {
                String input = scanner.nextLine();
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
    String[] parts = input.split(" ", 2);

    CommandEntry ce = commands.get(parts[0]);

    if (ce != null) {
      Consumer<String> command = commands.get(parts[0]).action();
      command.accept(parts.length > 1 ? parts[1] : "");
    } else {
      System.out.println(TextGetter.getText("unknownCommand", input));
      this.helpCommand("");
    }
  }

  /**
   * Displays the current state of the board and the next player.
   *
   * @param args Unused argument
   */
  private void displayBoardCommand(String args) {
    System.out.println(Game.getInstance().getGameRepresentation());
  }

  /**
   * Handles the history command by displaying the history of moves made in the game.
   *
   * @param args Unused argument
   */
  private void historyCommand(String args) {
    System.out.println(TextGetter.getText("historyTitle"));
    System.out.println(Game.getInstance().getHistory());
  }

  /**
   * Handles the move command.
   *
   * @param args The move in standard text notation.
   */
  private void moveCommand(String args) {
    BagOfCommands.getInstance().addCommand(new PlayMoveCommand(args));
  }

  /**
   * Handles the help command.
   *
   * @param args Unused argument
   */
  private void helpCommand(String args) {
    System.out.println(TextGetter.getText("availableCommandsTitle"));
    for (Map.Entry<String, CommandEntry> entry : commands.entrySet()) {
      System.out.printf("  %-10s - %s%n", entry.getKey(), entry.getValue().description());
    }
  }

  /**
   * Handles the save command.
   *
   * @param args The path to where the game should be saved.
   */
  private void saveCommand(String args) {
    BagOfCommands.getInstance().addCommand(new SaveGameCommand(args.strip()));
  }

  /**
   * Handles the draw command.
   *
   * @param args Unused argument
   */
  private void drawCommand(String args) {
    BagOfCommands.getInstance()
        .addCommand(new ProposeDrawCommand(Game.getInstance().getGameState().isWhiteTurn()));
  }

  /**
   * Handles the undraw command.
   *
   * @param args Unused argument
   */
  private void undrawCommand(String args) {
    BagOfCommands.getInstance()
        .addCommand(new CancelDrawCommand(Game.getInstance().getGameState().isWhiteTurn()));
  }

  /**
   * Handles the quit command.
   *
   * @param args Unused argument
   */
  private void quitCommand(String args) {
    System.out.println(TextGetter.getText("quitting"));
    this.running = false;
  }

  /**
   * Handles the undo command by reverting the last move in history.
   *
   * @param args Unused argument
   */
  private void undoCommand(String args) {
    BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
  }

  /**
   * Handles the redo command by re-executing a previously undone move.
   *
   * @param args Unused argument
   */
  private void redoCommand(String args) {
    BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
  }

  /**
   * Handles the surrender command.
   *
   * @param args Unused argument
   */
  private void surrenderCommand(String args) {
    BagOfCommands.getInstance()
        .addCommand(new SurrenderCommand(Game.getInstance().getGameState().isWhiteTurn()));
  }

  private void timeCommand(String args) {
    Timer timer = Game.getInstance().getGameState().getMoveTimer();
    if (timer != null) {
      System.out.println(
          TextGetter.getText(
              "timeRemainingCurrent",
              Game.getInstance().getGameState().getMoveTimer().getTimeRemainingString()));
    } else {
      System.out.println(TextGetter.getText("noTimer"));
    }
  }

  private record CommandEntry(Consumer<String> action, String description) {}
}
