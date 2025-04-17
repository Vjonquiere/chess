package pdp.model;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.error;
import static pdp.utils.OptionType.GUI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import pdp.controller.BagOfCommands;
import pdp.events.EventObserver;
import pdp.events.EventType;
import pdp.exceptions.FailedSaveException;
import pdp.exceptions.IllegalMoveException;
import pdp.model.ai.Solver;
import pdp.model.ai.algorithms.MonteCarloTreeSearch;
import pdp.model.board.Move;
import pdp.model.history.History;
import pdp.model.history.HistoryState;
import pdp.model.parsers.FenHeader;
import pdp.model.parsers.FileBoard;
import pdp.model.savers.BoardSaver;
import pdp.utils.Logging;
import pdp.utils.OptionType;
import pdp.utils.Position;
import pdp.utils.TextGetter;
import pdp.utils.Timer;

/** Model of our MVC architecture. Uses the Singleton design pattern. */
public final class Game extends GameAbstract {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

  /** Instance of Game, design pattern singleton. */
  private static Game instance;

  /** Solver of the White AI player. */
  private Solver solverWhite;

  /** Solver of the Black AI player. */
  private Solver solverBlack;

  /** Boolean to indicate whether the white player is an AI or not. */
  private boolean whiteAi;

  /** Boolean to indicate whether the black player is an AI or not. */
  private boolean blackAi;

  /** Boolean to indicate whether the game instance is initializing or is ready to use. */
  private boolean isInitializing;

  /** Boolean to indicate whether the game was load from a file or not. */
  private boolean loadedFromFile;

  /** Boolean to indicate whether the game was load from a file which had a history or not. */
  private boolean loadingFileWithHistory;

  /** Boolean to indicate whether the game is in contest mode or not. */
  private boolean isContestMode;

  /** Map containing the options of the game and their values. */
  private final Map<OptionType, String> options;

  /** Lock of the view, to avoid desynchronization between the view and the model. */
  private final ReentrantLock viewLock = new ReentrantLock();

  /** Condition of the lock. */
  private final Condition workingView = viewLock.newCondition();

  /** Boolean to indicate whether the view is on another thread (javafx) or not. */
  private final boolean viewOnOtherThread;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Private constructor for design pattern singleton.
   *
   * @param whiteAi true if the white player is an AI, false otherwise.
   * @param blackAi true if the black player is an AI, false otherwise.
   * @param solverWhite solver of the white AI player.
   * @param solverBlack solver of the white AI player.
   * @param gameState Game state of the game
   * @param history History of the game
   * @param options Options given in arguments or by default
   */
  private Game(
      final boolean whiteAi,
      final boolean blackAi,
      final Solver solverWhite,
      final Solver solverBlack,
      final GameState gameState,
      final History history,
      final Map<OptionType, String> options) {

    super(gameState, history, new HashMap<>());

    super.getGameState()
        .setSimplifiedZobristHashing(
            super.getZobristHasher()
                .generateSimplifiedHashFromBitboards(this.getGameState().getBoard()));
    this.addStateToCount(this.getGameState().getSimplifiedZobristHashing());
    this.getHistory()
        .addMove(
            new HistoryState(
                new Move(new Position(-1, -1), new Position(-1, -1)),
                this.getGameState().getCopy()));
    this.options = options;
    this.viewOnOtherThread = options.containsKey(GUI);
    this.whiteAi = whiteAi;
    this.blackAi = blackAi;
    this.solverWhite = solverWhite;
    this.solverBlack = solverBlack;

    if (instance != null) {
      if (instance.getTimer(true) != null) {
        instance.getTimer(true).stop();
      }
      if (instance.getTimer(false) != null) {
        instance.getTimer(false).stop();
      }

      if (instance.getBlackSolver() != null) {
        instance.getBlackSolver().stopSearch(false);
      }
      if (instance.getWhiteSolver() != null) {
        instance.getWhiteSolver().stopSearch(false);
      }

      for (final EventObserver observer : instance.getObservers()) {
        this.addObserver(observer);
      }

      for (final EventObserver observer : instance.getErrorObservers()) {
        this.addErrorObserver(observer);
      }
    }

    debug(LOGGER, "Game created");
  }

  /**
   * Retrieves the options of the game.
   *
   * @return map of options and values
   */
  public Map<OptionType, String> getOptions() {
    return options;
  }

  /**
   * Retrieves the timer of the player of the color given in parameters.
   *
   * @param isWhite true if the player is white, false for black player
   * @return timer of a player
   */
  public Timer getTimer(final boolean isWhite) {
    if (isWhite && this.whiteAi) {
      return this.solverWhite.getTimer();
    }
    if (!isWhite && this.blackAi) {
      return this.solverBlack.getTimer();
    }
    return super.getGameState().getMoveTimer();
  }

  /**
   * Retrieves the lock of the view.
   *
   * @return field viewLock
   */
  public ReentrantLock getViewLock() {
    return this.viewLock;
  }

  /**
   * Retrieves the Condition corresponding to the field workingView.
   *
   * @return field workingView;
   */
  public Condition getWorkingViewCondition() {
    return this.workingView;
  }

  /**
   * Returns a boolean corresponding to whether the current player is an AI.
   *
   * @return true if the current player is an AI
   */
  public boolean isCurrentPlayerAi() {
    final boolean player = super.getGameState().isWhiteTurn();
    if (player && this.whiteAi) {
      return true;
    }
    return !player && this.blackAi;
  }

  /**
   * Sets the value of the field isInitializing.
   *
   * @param isInit boolean to indicate if the game is initializing
   */
  public void setInitializing(final boolean isInit) {
    this.isInitializing = isInit;
  }

  /**
   * Returns whether the White player is controlled by an AI.
   *
   * @return true if the White player is an AI, false otherwise.
   */
  public boolean isWhiteAi() {
    return whiteAi;
  }

  /**
   * Returns whether the Black player is controlled by an AI.
   *
   * @return true if the Black player is an AI, false otherwise.
   */
  public boolean isBlackAi() {
    return blackAi;
  }

  /**
   * Assigns boolean value to whiteAi attribute field. Method used in GameInitializer.
   *
   * @param whiteAi true if white is AI. false otherwise.
   */
  public void setWhiteAi(final boolean whiteAi) {
    this.whiteAi = whiteAi;
  }

  /**
   * Assigns boolean value to blackAi attribute field. Method used in GameInitializer.
   *
   * @param blackAi true if black is AI. false otherwise.
   */
  public void setBlackAi(final boolean blackAi) {
    this.blackAi = blackAi;
  }

  /**
   * Gets the solver used by the White AI.
   *
   * @return the {@link Solver} instance used for White AI decision-making.
   */
  public Solver getWhiteSolver() {
    return solverWhite;
  }

  /**
   * Gets the solver used by the Black AI.
   *
   * @return the {@link Solver} instance used for Black AI decision-making.
   */
  public Solver getBlackSolver() {
    return solverBlack;
  }

  /**
   * Assigns solver for white. Method used in GameInitializer.
   *
   * @param solver The new solver to assign to white.
   */
  public void setWhiteSolver(final Solver solver) {
    this.solverWhite = solver;
  }

  /**
   * Assigns solver for black. Method used in GameInitializer.
   *
   * @param solver The new solver to assign to black.
   */
  public void setBlackSolver(final Solver solver) {
    this.solverBlack = solver;
  }

  /**
   * Method used in GameInitializer to set boolean value to indicate if the game was loaded from a
   * file. Boolean value is used in playMove() to know if history has to be overwritten.
   */
  public void setLoadedFromFile() {
    this.loadedFromFile = true;
  }

  /**
   * Indicates whether the game was loaded from a file or not.
   *
   * @return true if the game was loaded from a file, false otherwise
   */
  public boolean isLoadedFromFile() {
    return this.loadedFromFile;
  }

  /**
   * Retrieves the file the game was loaded from.
   *
   * @return the path of the file that generated the game
   */
  public String getContestFile() {
    return options.get(OptionType.CONTEST);
  }

  /**
   * Indicates whether the file loaded had a history.
   *
   * @return true if the file that was used to load the game has a history. false otherwise.
   */
  public boolean loadingFileHasHistory() {
    return this.loadingFileWithHistory;
  }

  /**
   * Method used in checkAndOverwriteHistory() to know how to handle new moves.
   *
   * @param fileHasHistory boolean value used to set private boolean loadingFileHasHistory.
   */
  public void setLoadingFileHasHistory(final boolean fileHasHistory) {
    this.loadingFileWithHistory = fileHasHistory;
  }

  /**
   * Method used in GameInitializer to set boolean value to indicate if the game was loaded from a
   * file with contest mode on.
   *
   * @param mode boolean to indicate if contest mode is on or off.
   */
  public void setContestMode(final boolean mode) {
    this.isContestMode = mode;
  }

  /**
   * Retrieves a boolean to indicate whether the contest mode is on or not.
   *
   * @return true if the game was loaded from a file with contest mode enabled. false otherwise.
   */
  public boolean isContestMode() {
    return this.isContestMode;
  }

  /**
   * Plays the first AI move if White AI is activated. The other calls to AI will be done in {@link
   * Game#updateGameStateAfterMove}
   */
  public void startAi() {
    if (whiteAi && this.getGameState().isWhiteTurn()) {
      this.notifyObservers(EventType.AI_PLAYING);
      solverWhite.playAiMove(this);
    } else if (blackAi && !this.getGameState().isWhiteTurn()) {
      this.notifyObservers(EventType.AI_PLAYING);
      solverBlack.playAiMove(this);
    }
  }

  /**
   * Adds an observer to the game and game state and immediately notifies a GAME_STARTED event.
   *
   * @param observer The observer to be added.
   */
  @Override
  public void addObserver(final EventObserver observer) {
    debug(LOGGER, "An observer have been attached to Game");
    super.addObserver(observer);
    if (super.getGameState() != null) {
      super.getGameState().addObserver(observer);
    }
    this.notifyObserver(observer, EventType.GAME_STARTED);
  }

  /**
   * Adds an observer to the game and game state that listens for error events.
   *
   * @param observer The observer to be added.
   */
  @Override
  public void addErrorObserver(final EventObserver observer) {
    debug(LOGGER, "An error observer have been attached to Game");
    super.addErrorObserver(observer);
    if (super.getGameState() != null) {
      super.getGameState().addErrorObserver(observer);
    }
  }

  /**
   * Creates a new instance of the Game class and stores it in the instance variable.
   *
   * @param isWhiteAi Whether the white player is an AI.
   * @param isBlackAi Whether the black player is an AI.
   * @param solverWhite The solver to use for White AI moves.
   * @param solverBlack The solver to use for Black AI moves.
   * @param options Options given in command line or by default.
   * @return The newly created instance of Game.
   */
  public static Game initialize(
      final boolean isWhiteAi,
      final boolean isBlackAi,
      final Solver solverWhite,
      final Solver solverBlack,
      final Timer timer,
      final Map<OptionType, String> options) {
    return initialize(isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, null, options);
  }

  /**
   * Creates a new instance of the Game class and stores it in the instance variable.
   *
   * @param isWhiteAi Whether the white player is an AI.
   * @param isBlackAi Whether the black player is an AI.
   * @param solverWhite The solver to use for White AI moves.
   * @param solverBlack The solver to use for Black AI moves.
   * @param board The board state to use
   * @param options Options given in command line or by default.
   * @return The newly created instance of Game.
   */
  public static Game initialize(
      final boolean isWhiteAi,
      final boolean isBlackAi,
      final Solver solverWhite,
      final Solver solverBlack,
      final Timer timer,
      final FileBoard board,
      final Map<OptionType, String> options) {
    debug(LOGGER, board == null ? "Initializing Game..." : "Initializing Game from given board...");
    instance =
        createGameInstance(isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, board, options);
    setupTimer(timer);
    debug(LOGGER, "Game initialized!");
    instance.notifyObservers(EventType.GAME_STARTED);
    return instance;
  }

  /**
   * Creates a new instance of the Game class with the given parameters.
   *
   * @param isWhiteAi Whether the white player is an AI.
   * @param isBlackAi Whether the black player is an AI.
   * @param solverWhite The solver to use for White AI moves.
   * @param solverBlack The solver to use for Black AI moves.
   * @param timer The timer to use for the game.
   * @param board The board state to use, or null for a default board.
   * @param options Options given in command line or by default.
   * @return The newly created instance of Game.
   */
  private static Game createGameInstance(
      final boolean isWhiteAi,
      final boolean isBlackAi,
      final Solver solverWhite,
      final Solver solverBlack,
      final Timer timer,
      final FileBoard board,
      final Map<OptionType, String> options) {
    final GameState gameState =
        (board == null) ? new GameState(timer) : new GameState(board, timer);
    final Game game =
        new Game(isWhiteAi, isBlackAi, solverWhite, solverBlack, gameState, new History(), options);
    BagOfCommands.getInstance().setModel(game);
    return game;
  }

  /**
   * Sets up the timer for the game. If the timer is not null, it assigns a callback for when the
   * timer runs out and starts the timer if the current player is not an AI.
   *
   * @param timer The timer to be set up for the game.
   */
  private static void setupTimer(final Timer timer) {
    if (timer != null) {
      timer.setCallback(instance::outOfTimeCallback);
      if (!instance.isCurrentPlayerAi()) {
        timer.start();
      }
    }
  }

  /** When out of time, the callback will be sent and the game will end. */
  public void outOfTimeCallback() {
    debug(LOGGER, "outOfTimeCallback called");
    super.getGameState().playerOutOfTime(super.getGameState().isWhiteTurn());
  }

  /**
   * Tries to play the given move on the game.
   *
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is not legal.
   */
  @Override
  public void playMove(final Move move) {
    final Position sourcePosition = new Position(move.getSource().x(), move.getSource().y());
    final Position destPosition = new Position(move.getDest().x(), move.getDest().y());
    debug(LOGGER, "Trying to play move [" + sourcePosition + ", " + destPosition + "]");

    if (!super.validatePieceOwnership(super.getGameState(), sourcePosition)) {
      throw new IllegalMoveException(move.toString());
    }
    super.validatePromotionMove(move);

    final List<Move> availableMoves =
        super.getGameState().getBoard().getAvailableMoves(sourcePosition);
    final Optional<Move> classicalMove = move.isMoveClassical(availableMoves);

    final Move moveToProcess;
    if (classicalMove.isPresent()) {
      moveToProcess = classicalMove.get();
      super.processMove(super.getGameState(), moveToProcess);
    } else {
      throw new IllegalMoveException(move.toString());
    }

    this.updateGameStateAfterMove(moveToProcess);
  }

  /**
   * Updates the game state after a move is played.
   *
   * <p>The game state is updated by:
   *
   * <ul>
   *   <li>Incrementing the full turn number if the move was made by white.
   *   <li>Adding the move to the history.
   *   <li>Switching the current player turn.
   *   <li>Updating the board player.
   *   <li>Updating the simplified zobrist hashing.
   *   <li>Checking for threefold repetition.
   *   <li>Checking the game status, which may end the game.
   *   <li>Notifying observers that a move has been played.
   * </ul>
   */
  @Override
  protected void updateGameStateAfterMove(final Move move) {

    if (super.getGameState().getMoveTimer() != null
        && !this.isCurrentPlayerAi()
        && !isInitializing
        && !super.getGameState().isGameOver()) {
      super.getGameState().getMoveTimer().stop();
    }

    super.updateGameStateAfterMove(move);

    debug(LOGGER, "Checking phase of the game (endgame, middle game, etc.)...");
    if (isEndGamePhase()) {
      if (this.solverWhite != null
          && (!(this.solverWhite.getAlgorithm() instanceof MonteCarloTreeSearch))
          && (this.solverWhite.getCurrentHeuristic() != this.solverWhite.getEndgameHeuristic())) {
        // Set endgame heuristic only once and only if endgame phase
        this.solverWhite.setHeuristic(this.solverWhite.getEndgameHeuristic());
      }
      if (this.solverBlack != null
          && (!(this.solverBlack.getAlgorithm() instanceof MonteCarloTreeSearch))
          && (this.solverBlack.getCurrentHeuristic() != this.solverBlack.getEndgameHeuristic())) {
        this.solverBlack.setHeuristic(this.solverBlack.getEndgameHeuristic());
      }
    }

    if (!isEndGamePhase()) {
      if (this.solverWhite != null
          && (!(this.solverWhite.getAlgorithm() instanceof MonteCarloTreeSearch))
          && (this.solverWhite.getCurrentHeuristic() != this.solverWhite.getStartHeuristic())) {
        this.solverWhite.setHeuristic(this.solverWhite.getStartHeuristic());
      }
      if (this.solverBlack != null
          && (!(this.solverBlack.getAlgorithm() instanceof MonteCarloTreeSearch))
          && (this.solverBlack.getCurrentHeuristic() != this.solverBlack.getStartHeuristic())) {
        this.solverBlack.setHeuristic(this.solverBlack.getStartHeuristic());
      }
    }

    if (!isInitializing) {
      if (this.isContestMode()) {
        saveGame(getContestFile());
      }
      this.notifyObservers(EventType.MOVE_PLAYED);
      if (this.isContestMode()) {
        return;
      }
    }
    if (super.getGameState().getMoveTimer() != null
        && !this.isCurrentPlayerAi()
        && !super.getGameState().isGameOver()) {
      super.getGameState().getMoveTimer().start();
    }

    if (!isInitializing
        && !isOver()
        && !isContestMode()
        && ((super.getGameState().isWhiteTurn() && whiteAi)
            || (!super.getGameState().isWhiteTurn() && blackAi))) {

      if (viewOnOtherThread) {
        viewLock.lock();
        this.notifyObservers(EventType.AI_PLAYING);
        try {
          workingView.await();
        } catch (InterruptedException e) {
          error(e.toString());
        } finally {
          viewLock.unlock();
        }
      } else {
        this.notifyObservers(EventType.AI_PLAYING);
      }
      if (super.getGameState().isWhiteTurn()) {
        solverWhite.playAiMove(this);
      } else {
        solverBlack.playAiMove(this);
      }
    }
  }

  /**
   * Saves the current game state to a file.
   *
   * <p>The saved file contains the current position of the board followed by the move history of
   * the game in standard algebraic notation.
   *
   * @param path The path to the file to write to.
   * @throws FailedSaveException If the file cannot be written to.
   */
  public void saveGame(final String path) {
    final boolean[] castlingRights = getBoard().getCastlingRights();
    final String board =
        BoardSaver.saveBoard(
            new FileBoard(
                this.getBoard(),
                this.getGameState().isWhiteTurn(),
                new FenHeader(
                    castlingRights[0],
                    castlingRights[1],
                    castlingRights[2],
                    castlingRights[3],
                    getBoard().getEnPassantPos(),
                    getBoard().getNbMovesWithNoCaptureOrPawn(),
                    getGameState().getFullTurn())));
    final String gameStr = super.getHistory().toAlgebraicString();

    final String game = board + "\n" + gameStr;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      writer.write(game);
    } catch (IOException e) {
      debug(LOGGER, "Error writing to file: " + e.getMessage());
      throw new FailedSaveException(path);
    }
    debug(LOGGER, "Game saved to " + path);
    this.notifyObservers(EventType.GAME_SAVED);
  }

  /**
   * Retrieves the history of moves in the current game as a formatted string.
   *
   * @return A string representation of the game's move history.
   */
  public String getStringHistory() {
    return super.getHistory().toString();
  }

  /** Restarts the game by resetting the game state and history. */
  public void restartGame() {

    debug(LOGGER, "Restarting game");

    if (instance.getTimer(true) != null) {
      instance.getTimer(true).stop();
    }
    if (instance.getTimer(false) != null) {
      instance.getTimer(false).stop();
    }

    super.getGameState().updateFrom(new GameState(super.getGameState().getMoveTimer()));
    super.getHistory().clear();

    super.getHistory()
        .addMove(
            new HistoryState(
                new Move(new Position(-1, -1), new Position(-1, -1)),
                super.getGameState().getCopy()));

    super.getStateCount().clear();
    super.getGameState()
        .setSimplifiedZobristHashing(
            super.getZobristHasher()
                .generateSimplifiedHashFromBitboards(super.getGameState().getBoard()));
    this.addStateToCount(super.getGameState().getSimplifiedZobristHashing());

    this.notifyObservers(EventType.GAME_RESTART);

    if (super.getGameState().getMoveTimer() != null) {
      if (!instance.isCurrentPlayerAi()) {
        super.getGameState().getMoveTimer().start();
      }
    }

    this.startAi();

    debug(LOGGER, "Game restarted");
  }

  /**
   * Initializes a new Game object from a list of moves.
   *
   * <p>The new game is initialized with the given AI settings, solver, and starting position.
   *
   * @param moves The moves to play in sequence.
   * @param isWhiteAi Whether the white player is an AI.
   * @param isBlackAi Whether the black player is an AI.
   * @param solverWhite The solver to use for White AI moves.
   * @param solverBlack The solver to use for Black AI moves.
   * @param timer The timer to use for the game.
   * @param options Options given in command line or by default.
   * @return A new Game object with the given moves played.
   * @throws IllegalMoveException If any of the given moves are illegal.
   */
  public static Game fromHistory(
      final List<Move> moves,
      final boolean isWhiteAi,
      final boolean isBlackAi,
      final Solver solverWhite,
      final Solver solverBlack,
      final Timer timer,
      final Map<OptionType, String> options) {
    instance =
        new Game(
            isWhiteAi,
            isBlackAi,
            solverWhite,
            solverBlack,
            new GameState(timer),
            new History(),
            options);
    BagOfCommands.getInstance().setModel(instance);
    instance.setInitializing(true);
    for (final Move move : moves) {
      instance.playMove(move);
    }

    if (timer != null) {
      timer.setCallback(instance::outOfTimeCallback);
      if (!instance.isCurrentPlayerAi()) {
        timer.start();
      }
    }

    instance.setInitializing(false);
    instance.notifyObservers(EventType.GAME_STARTED);

    return instance;
  }

  /**
   * Returns a string representation of the game. Includes the ASCII representation of the board,
   * the time remaining (if timer is not null), and the color of the player to play.
   *
   * @return A string representation of the game.
   */
  public String getGameRepresentation() {
    final char[][] board = super.getGameState().getBoard().getAsciiRepresentation();
    final StringBuilder stringBuilder = new StringBuilder();

    final Timer timer = this.getTimer(!super.getGameState().isWhiteTurn());
    if (timer != null) {
      stringBuilder.append(TextGetter.getText("timeRemaining", timer.getTimeRemainingString()));
    }

    stringBuilder.append('\n');

    final int size = board.length;

    for (int row = 0; row < size; row++) {
      stringBuilder.append(size - row).append(" | ");
      for (int col = 0; col < size; col++) {
        stringBuilder.append(board[row][col]).append(' ');
      }
      stringBuilder.append('\n');
    }

    stringBuilder.append("    "); // Offset for row numbers
    stringBuilder.append("- ".repeat(size));
    stringBuilder.append("\n    ");
    for (char c = 'A'; c < 'A' + size; c++) {
      stringBuilder.append(c).append(' ');
    }
    stringBuilder.append("\n\n");

    if (!super.getGameState().isGameOver()) {
      stringBuilder.append(
          TextGetter.getText(
              "toPlay",
              super.getGameState().isWhiteTurn()
                  ? TextGetter.getText("white")
                  : TextGetter.getText("black")));
    } else {
      stringBuilder.append(TextGetter.getText("gameOver"));
    }

    stringBuilder.append('\n');

    return stringBuilder.toString();
  }

  /**
   * Retrieves the singleton instance of the Game.
   *
   * @return The single instance of Game.
   * @throws IllegalStateException If the Game has not been initialized.
   */
  public static Game getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Game has not been initialized");
    }
    return instance;
  }

  /**
   * Indicates whether the object has been fully initialized.
   *
   * @return {@code true} if initialization is complete, otherwise {@code false}.
   */
  public boolean isInitialized() {
    return this.isInitializing;
  }
}
