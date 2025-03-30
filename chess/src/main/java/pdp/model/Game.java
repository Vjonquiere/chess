package pdp.model;

import static pdp.utils.Logging.*;
import static pdp.utils.OptionType.GUI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import pdp.model.board.PromoteMove;
import pdp.model.history.History;
import pdp.model.history.HistoryNode;
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

  /**
   * Boolean to indicate whether an AI is searching for a move, avoid sending message to the view.
   */
  private boolean explorationAi;

  /** Boolean to indicate whether the game instance is initializing or is ready to use. */
  private boolean isInitializing;

  /** Boolean to indicate whether the game was load from a file or not. */
  private boolean loadedFromFile;

  /** Boolean to indicate whether the game was load from a file which had a history or not. */
  private boolean loadingFileWithHistory;

  /** Map containing the different options to parametrize the game and their values. */
  private boolean contestModeOn;

  /** Boolean value used to know if Ai played its move (contest Mode). */
  private boolean aiPlayedItsLastMove;

  /** Map containing th options of the game and their values. */
  private final HashMap<OptionType, String> options;

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
      final HashMap<OptionType, String> options) {

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
    this.explorationAi = false;
    this.solverWhite = solverWhite;
    this.solverBlack = solverBlack;
    this.aiPlayedItsLastMove = false;

    if (instance != null) {
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
  public HashMap<OptionType, String> getOptions() {
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
   * Assigns boolean value to blakAi attribute field. Method used in GameInitializer.
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
   * Sets the exploration field to the given boolean. Use it in the solver.
   *
   * @param exploration boolean corresponding to the new rights of explorationAI.
   */
  public void setExploration(final boolean exploration) {
    this.explorationAi = exploration;
  }

  /**
   * Indicates whether the AI is exploring (playing moves in its algorithm).
   *
   * @return True if it is exploring, False otherwise
   */
  public boolean isAiExploring() {
    return this.explorationAi;
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
   * Used in updateGameStateAfterMove() method to know how to handle game save.
   *
   * @return true if AI finished computing moves. false otherwise.
   */
  public boolean hasAiPlayedItsLastMove() {
    return this.aiPlayedItsLastMove;
  }

  /**
   * Method used in Solver.playAIMove(this) to indicate when AI finished computing. Used to know
   * when the game can be saved when loading or contest mode.
   */
  public void setAiPlayedItsLastMove(final boolean lastMove) {
    this.aiPlayedItsLastMove = lastMove;
  }

  /**
   * Method used in GameInitializer to set boolean value to indicate if the game was loaded from a
   * file with contest mode on.
   *
   * @param mode boolean to indicate if contest mode is on or off.
   */
  public void setContestModeOnOrOff(final boolean mode) {
    this.contestModeOn = mode;
  }

  /**
   * Retrieves a boolean to indicate whether the contest mode is on or not.
   *
   * @return true if the game was loaded from a file with contest mode enabled. false otherwise.
   */
  public boolean isContestModeOn() {
    return this.contestModeOn;
  }

  /**
   * Plays the first AI move if White AI is activated. The other calls to AI will be done in {@link
   * Game#updateGameStateAfterMove}
   */
  public void startAi() {
    if (whiteAi && this.getGameState().isWhiteTurn()) {
      this.notifyObservers(EventType.AI_PLAYING);
      solverWhite.playAiMove(this);
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
      final HashMap<OptionType, String> options) {
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
      final HashMap<OptionType, String> options) {
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
      final HashMap<OptionType, String> options) {
    GameState gameState = (board == null) ? new GameState(timer) : new GameState(board, timer);
    Game game =
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
  private static void setupTimer(Timer timer) {
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
  private void updateGameStateAfterMove(final Move move) {

    if (super.getGameState().getMoveTimer() != null
        && !this.isCurrentPlayerAi()
        && !explorationAi
        && !isInitializing
        && !super.getGameState().isGameOver()) {
      super.getGameState().getMoveTimer().stop();
    }

    if (super.getGameState().isWhiteTurn()) {
      super.getGameState().incrementsFullTurn();
    }

    super.getGameState().switchPlayerTurn();
    if (move.isCastle() || move instanceof PromoteMove) {
      super.getGameState()
          .setSimplifiedZobristHashing(
              super.getZobristHasher()
                  .updateSimplifiedHashFromBitboards(
                      super.getGameState().getSimplifiedZobristHashing(), getBoard(), move));
    } else {
      super.getGameState()
          .setSimplifiedZobristHashing(
              super.getZobristHasher().generateSimplifiedHashFromBitboards(getBoard()));
    }

    debug(LOGGER, "Checking threefold repetition...");
    final boolean threefoldRep =
        super.addStateToCount(super.getGameState().getSimplifiedZobristHashing());

    if (threefoldRep) {
      super.getGameState().activateThreefold();
    }

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
          && (this.solverBlack.getCurrentHeuristic() != this.solverWhite.getEndgameHeuristic())) {
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
          && (this.solverBlack.getCurrentHeuristic() != this.solverWhite.getStartHeuristic())) {
        this.solverBlack.setHeuristic(this.solverBlack.getStartHeuristic());
      }
    }
    debug(LOGGER, "Checking game status...");
    super.getGameState().checkGameStatus();

    // Check for history overwrite
    if (!isLoadedFromFile()) {
      super.getHistory().addMove(new HistoryState(move, super.getGameState().getCopy()));
    } else {
      if (!hasAiPlayedItsLastMove()) {
        if (isBlackAi() || isWhiteAi()) {
          super.getHistory().addMove(new HistoryState(move, super.getGameState().getCopy()));
        } else {
          checkAndOverwriteHistory(move);
        }
      } else {
        checkAndOverwriteHistory(move);
      }
    }

    if (!explorationAi && !isInitializing) {
      this.notifyObservers(EventType.MOVE_PLAYED);
    }
    if (super.getGameState().getMoveTimer() != null
        && !this.isCurrentPlayerAi()
        && !explorationAi
        && !super.getGameState().isGameOver()) {
      super.getGameState().getMoveTimer().start();
    }

    if (!explorationAi
        && !isInitializing
        && !isOver()
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
   * Checks if the move in parameter (the one we would like to play) matches or not the next move in
   * history. If not, overwrite history. This is used for games that are loaded from files.
   *
   * @param move the move we want to play in the game.
   */
  private void checkAndOverwriteHistory(final Move move) {
    final Optional<HistoryNode> currentNode = this.getHistory().getCurrentMove();

    if (loadingFileHasHistory()) {
      final Optional<HistoryNode> nextNode = currentNode.flatMap(HistoryNode::getNext);
      HistoryState nextState = null;
      if (nextNode.isPresent()) {
        nextState = nextNode.get().getState();
      }
      if (nextState == null) {
        // End of history already, so add new move and save
        super.getHistory().addMove(new HistoryState(move, super.getGameState().getCopy()));
        if (isContestModeOn()) {
          saveGame(getContestFile());
          debug(
              LOGGER,
              "Move differs from history. Overwriting history for file :" + getContestFile());
        } else {
          debug(LOGGER, "Move differs from history. Overwriting history");
        }
      } else {
        // Check if move we want to play is the same as the next one. If not, overwrite history and
        // save
        if (!move.equals(nextState.getMove())) {
          if (isContestModeOn()) {
            super.getHistory().addMove(new HistoryState(move, super.getGameState().getCopy()));
            saveGame(getContestFile());
            debug(
                LOGGER,
                "Move differs from history. Overwriting history for file :" + getContestFile());
          } else {
            // Truncate history by adding a new move from this point forward
            super.getHistory().addMove(new HistoryState(move, super.getGameState().getCopy()));
            debug(LOGGER, "Move differs from history. Overwriting history");
          }

        } else {
          // If same move, just forward by one in the history
          super.getGameState().updateFrom(nextNode.get().getState().getGameState().getCopy());
          super.getHistory().setCurrentMove(currentNode.get().getNext().get());
          final long currBoardZobrist = super.getGameState().getSimplifiedZobristHashing();
          this.getStateCount()
              .put(currBoardZobrist, this.getStateCount().getOrDefault(currBoardZobrist, 0) + 1);
        }
      }
    } else {
      // If no history just add the move
      super.getHistory().addMove(new HistoryState(move, super.getGameState().getCopy()));
      if (isContestModeOn()) {
        print("LOADING FILE : " + getContestFile());
        saveGame(getContestFile());
        debug(
            LOGGER, "Move differs from history. Overwriting history for file :" + getContestFile());
      } else {
        debug(LOGGER, "Move differs from history. Overwriting history");
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
      final HashMap<OptionType, String> options) {
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
}
