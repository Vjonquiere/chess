package pdp.view;

import static java.lang.Thread.sleep;
import static pdp.utils.Logging.DEBUG;

import java.util.Arrays;
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
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.model.parsers.FENparser;
import pdp.model.parsers.FileBoard;
import pdp.utils.Logging;

public class UCIView implements View {
  private boolean running = false;
  private final Map<String, CommandEntry> commands = new HashMap<>();
  private static final Logger LOGGER = Logger.getLogger(UCIView.class.getName());

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
    Game.getInstance().viewLock.lock();
    try {
      switch (event) {
        case GAME_STARTED:
          break;
        case MOVE_PLAYED:
          break;
        case WIN_WHITE:
          break;
        case WIN_BLACK:
          break;
        case DRAW:
          break;
        case WHITE_DRAW_PROPOSAL:
          break;
        case BLACK_DRAW_PROPOSAL:
          break;
        case WHITE_UNDRAW:
          break;
        case BLACK_UNDRAW:
          break;
        case DRAW_ACCEPTED:
          break;
        case GAME_SAVED:
          break;
        case MOVE_UNDO:
          break;
        case WHITE_UNDO_PROPOSAL:
          break;
        case BLACK_UNDO_PROPOSAL:
          break;
        case MOVE_REDO:
          break;
        case WHITE_REDO_PROPOSAL:
          break;
        case BLACK_REDO_PROPOSAL:
          break;
        case OUT_OF_TIME_WHITE:
          break;
        case OUT_OF_TIME_BLACK:
          break;
        case THREEFOLD_REPETITION:
          break;
        case INSUFFICIENT_MATERIAL:
          break;
        case FIFTY_MOVE_RULE:
          break;
        case WHITE_RESIGNS:
          break;
        case BLACK_RESIGNS:
          break;
        case CHECKMATE_WHITE:
          break;
        case CHECKMATE_BLACK:
          break;
        case STALEMATE:
          break;
        case AI_PLAYING:
          break;
        case GAME_RESTART:
          break;
        default:
          DEBUG(LOGGER, "Received unknown game event: " + event);
          break;
      }
      Game.getInstance().workingView.signal();
    } finally {
      Game.getInstance().viewLock.unlock();
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
    // System.out.println(input);
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
    // System.out.println("SET POS " + args);
    if (args2.length > 2 && args2[0].equals("fen")) {
      String newArgs = String.join(" ", Arrays.copyOfRange(args2, 1, args2.length));
      System.err.println(newArgs);
      FileBoard f = FENparser.loadBoardFromFen(newArgs);
      Game.initialize(
          Game.getInstance().isWhiteAI(),
          Game.getInstance().isBlackAI(),
          Game.getInstance().getWhiteSolver(),
          Game.getInstance().getBlackSolver(),
          Game.getInstance().getGameState().getMoveTimer(),
          f,
          Game.getInstance().getOptions());
      DEBUG(LOGGER, Game.getInstance().getGameRepresentation());
      try {
        sleep(2000);
      } catch (Exception e) {

      }
    } else if (args2.length > 2 && args2[0].equals("startpos")) {
      Game.initialize(
          false,
          false,
          null,
          null,
          Game.getInstance().getGameState().getMoveTimer(),
          Game.getInstance().getOptions());
      // System.out.println(Game.getInstance().getGameRepresentation());
      for (int i = 2; i < args2.length; i++) {
        // System.out.println(args2[i]);
        Game.getInstance().playMove(Move.fromUCIString(args2[i]));
        // System.out.println("Move done");
      }
      // System.out.println(Game.getInstance().getGameRepresentation());

    } else if (args2.length == 1 && args2[0].equals("startpos")) {
      // System.out.println("FROM BEGINNING");
      Game.initialize(
          false,
          false,
          null,
          null,
          Game.getInstance().getGameState().getMoveTimer(),
          Game.getInstance().getOptions());
    }
    // Runtime.getRuntime().exit(1);
    // goCommand("");
  }

  private void goCommand(String args) {
    Solver solver = new Solver();
    DEBUG(LOGGER, "Searching for best move");
    System.out.println("bestmove " + solver.getBestMove(Game.getInstance()).toUciString());
  }

  private void isReadyCommand(String args) {
    /*try {
      sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }*/

    System.out.println("readyok");
  }

  private void quitCommand(String args) {
    Runtime.getRuntime().exit(0);
  }

  private void uciNewGameCommand(String args) {}

  private record CommandEntry(Consumer<String> action, String description) {}
}
