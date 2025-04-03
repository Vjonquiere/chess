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
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Logging;

/** View used to communicate with other chess engines. */
public class UciView implements View {
  /** Boolean to indicate whether the view is running or not. */
  private boolean running = false;

  /** Map making a correspondance between a string and the command it represents. */
  private final Map<String, CommandEntry> commands = new HashMap<>();

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(UciView.class.getName());

  /** Solver to make the moves against another AI. */
  private final Solver solver = new Solver();

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Initializes the view by settings the commands and parametrizing the game with the correct fifty
   * move rule and threefold repetition. The other chess engine use a 5-fold repetition and a 75
   * move rule, we adapt our game the same way.
   */
  public UciView() {
    commands.put("uci", new CommandEntry(this::uciCommand, "uci"));
    commands.put("ucinewgame", new CommandEntry(this::uciNewGameCommand, "uci new game"));
    commands.put("position", new CommandEntry(this::positionCommand, "position"));
    commands.put("go", new CommandEntry(this::goCommand, "go"));
    commands.put("isready", new CommandEntry(this::isReadyCommand, "isReady"));
    commands.put("quit", new CommandEntry(this::quitCommand, "quit"));
    GameAbstract.setThreeFoldLimit(5);
    GameState.setFiftyMoveLimit(75);
    solver.setEndgameHeuristic(HeuristicType.STANDARD);
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
    switch (event) {
      case WIN_BLACK -> print("Black won!");
      case WIN_WHITE -> print("White won!");
      case THREEFOLD_REPETITION -> print(Game.getThreeFoldLimit() + " fold repetition!");
      case FIFTY_MOVE_RULE -> print(GameState.getFiftyMoveLimit() + " move rule!");
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
    input = input.trim();
    final String[] parts = input.split(" ", 2);

    final CommandEntry commandEntry = commands.get(parts[0]);

    if (commandEntry != null) {
      final Consumer<String> command = commands.get(parts[0]).action();
      command.accept(parts.length > 1 ? parts[1] : "");
    } else {
      print("unknown command: " + input + " received\n");
    }
  }

  private void uciCommand(final String args) {
    print("Chess 2\nMade by PDP team\nuciok\n");
    // TODO: add ai config
  }

  private void positionCommand(final String args) {
    final String[] args2 = args.split(" ");
    if (args2.length > 2 && args2[0].equals("fen")) {
      print("can't handle fen boards");
    } else if (args2.length > 2 && args2[0].equals("startpos")) {
      Game.initialize(
          false,
          false,
          null,
          null,
          Game.getInstance().getGameState().getMoveTimer(),
          Game.getInstance().getOptions());
      for (int i = 2; i < args2.length; i++) {
        Game.getInstance().playMove(Move.fromUciString(args2[i]));
      }

    } else if (args2.length == 1 && args2[0].equals("startpos")) {
      Game.initialize(
          false,
          false,
          null,
          null,
          Game.getInstance().getGameState().getMoveTimer(),
          Game.getInstance().getOptions());
    }
  }

  private void goCommand(String args) {
    debug(LOGGER, "Searching for best move");
    final Move move = solver.getBestMove(Game.getInstance());
    if (move == null) {
      error(Game.getInstance().getGameRepresentation());
    }
    print("bestmove " + move.toUciString());
  }

  private void isReadyCommand(String args) {
    print("readyok");
  }

  private void quitCommand(String args) {
    Runtime.getRuntime().exit(0);
  }

  private void uciNewGameCommand(String args) {}

  private record CommandEntry(Consumer<String> action, String description) {}
}
