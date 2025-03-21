package pdp.view;

import static pdp.utils.Logging.DEBUG;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Logger;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.*;
import pdp.events.EventType;
import pdp.exceptions.*;
import pdp.model.Game;
import pdp.model.GameAbstract;
import pdp.model.GameState;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Logging;

public class UCIView implements View {
  private boolean running = false;
  private final Map<String, CommandEntry> commands = new HashMap<>();
  private static final Logger LOGGER = Logger.getLogger(UCIView.class.getName());
  private final Solver solver = new Solver();

  static {
    Logging.configureLogging(LOGGER);
  }

  public UCIView() {
    commands.put("uci", new CommandEntry(this::uciCommand, "uci"));
    commands.put("ucinewgame", new CommandEntry(this::uciNewGameCommand, "uci new game"));
    commands.put("position", new CommandEntry(this::positionCommand, "position"));
    commands.put("go", new CommandEntry(this::goCommand, "go"));
    commands.put("isready", new CommandEntry(this::isReadyCommand, "isReady"));
    commands.put("quit", new CommandEntry(this::quitCommand, "quit"));
    GameAbstract.THREE_FOLD_REPETITION = 5;
    GameState.FIFTY_MOVE_RULE = 75;
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
  public void onGameEvent(EventType event) {
    switch (event) {
      case WIN_BLACK -> System.out.println("Black won!");
      case WIN_WHITE -> System.out.println("White won!");
      case THREEFOLD_REPETITION ->
          System.out.println(Game.THREE_FOLD_REPETITION + " fold repetition!");
      case FIFTY_MOVE_RULE -> System.out.println(GameState.FIFTY_MOVE_RULE + " move rule!");
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
      System.out.println(Game.getInstance().getGameRepresentation());
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
    input = input.trim();
    String[] parts = input.split(" ", 2);

    CommandEntry ce = commands.get(parts[0]);

    if (ce != null) {
      Consumer<String> command = commands.get(parts[0]).action();
      command.accept(parts.length > 1 ? parts[1] : "");
    } else {
      System.out.println("unknown command: " + input + " received\n");
    }
  }

  private void uciCommand(String args) {
    System.out.println("Chess 2\nMade by PDP team\nuciok\n");
    // TODO: add ai config
  }

  private void positionCommand(String args) {
    String[] args2 = args.split(" ");
    if (args2.length > 2 && args2[0].equals("fen")) {
      System.out.println("can't handle fen boards");
    } else if (args2.length > 2 && args2[0].equals("startpos")) {
      Game.initialize(
          false,
          false,
          null,
          null,
          Game.getInstance().getGameState().getMoveTimer(),
          Game.getInstance().getOptions());
      for (int i = 2; i < args2.length; i++) {
        Game.getInstance().playMove(Move.fromUCIString(args2[i]));
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
    DEBUG(LOGGER, "Searching for best move");
    Move move = solver.getBestMove(Game.getInstance());
    if (move == null) {
      System.err.println(Game.getInstance().getGameRepresentation());
    }
    System.out.println("bestmove " + move.toUciString());
  }

  private void isReadyCommand(String args) {
    System.out.println("readyok");
  }

  private void quitCommand(String args) {
    Runtime.getRuntime().exit(0);
  }

  private void uciNewGameCommand(String args) {}

  private record CommandEntry(Consumer<String> action, String description) {}
}
