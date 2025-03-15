package pdp.model;

import static pdp.utils.Logging.DEBUG;
import static pdp.utils.OptionType.GUI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import pdp.controller.BagOfCommands;
import pdp.events.EventObserver;
import pdp.events.EventType;
import pdp.events.Subject;
import pdp.exceptions.FailedRedoException;
import pdp.exceptions.FailedSaveException;
import pdp.exceptions.FailedUndoException;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPromoteFormatException;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.ai.algorithms.MonteCarloTreeSearch;
import pdp.model.ai.heuristics.EndGameHeuristic;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.board.ZobristHashing;
import pdp.model.history.History;
import pdp.model.history.HistoryNode;
import pdp.model.history.HistoryState;
import pdp.model.parsers.FenHeader;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.model.savers.FENSaver;
import pdp.utils.Logging;
import pdp.utils.OptionType;
import pdp.utils.Position;
import pdp.utils.TextGetter;
import pdp.utils.Timer;

public class Game extends Subject {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static Game instance;
  private static ZobristHashing zobristHashing = new ZobristHashing();
  private GameState gameState;
  private Solver solverWhite;
  private Solver solverBlack;
  private boolean isWhiteAI;
  private boolean isBlackAI;
  private boolean explorationAI;
  private History history;
  private HashMap<Long, Integer> stateCount;
  private HashMap<OptionType, String> options;
  public final Lock viewLock = new ReentrantLock();
  public final Condition workingView = viewLock.newCondition();
  private final boolean VIEW_ON_OTHER_THREAD;

  static {
    Logging.configureLogging(LOGGER);
  }

  private Game(
      boolean isWhiteAI,
      boolean isBlackAI,
      Solver solverWhite,
      Solver solverBlack,
      GameState gameState,
      History history,
      HashMap<OptionType, String> options) {

    this.gameState = gameState;
    this.options = options;
    this.VIEW_ON_OTHER_THREAD = options.containsKey(GUI);
    this.isWhiteAI = isWhiteAI;
    this.isBlackAI = isBlackAI;
    this.explorationAI = false;
    this.solverWhite = solverWhite;
    this.solverBlack = solverBlack;
    this.history = history;
    this.history.addMove(
        new HistoryState(
            new Move(new Position(-1, -1), new Position(-1, -1)), this.gameState.getCopy()));
    this.stateCount = new HashMap<>();
    // this.gameState.setZobristHashing(zobristHashing.generateHashFromBitboards(this.gameState.getBoard()));
    this.gameState.setSimplifiedZobristHashing(
        zobristHashing.generateSimplifiedHashFromBitboards(this.gameState.getBoard()));
    this.addStateToCount(this.gameState.getSimplifiedZobristHashing());

    if (instance != null) {
      for (EventObserver observer : instance.getObservers()) {
        this.addObserver(observer);
      }

      for (EventObserver observer : instance.getErrorObservers()) {
        this.addErrorObserver(observer);
      }
    }

    DEBUG(LOGGER, "Game created");
  }

  /**
   * Add a state to the count of seen states. If the state has been seen 3 times, returns true.
   *
   * @param simplifiedZobristHashing the simplified Zobrist hashing of the state
   * @return true if the state has been seen 3 times, false otherwise
   */
  private boolean addStateToCount(long simplifiedZobristHashing) {
    DEBUG(LOGGER, "Adding hash [" + simplifiedZobristHashing + "] to count");
    if (this.stateCount.containsKey(simplifiedZobristHashing)) {
      this.stateCount.put(
          simplifiedZobristHashing, this.stateCount.get(simplifiedZobristHashing) + 1);

      if (this.stateCount.get(simplifiedZobristHashing) == 3) {
        DEBUG(LOGGER, "State with hash " + simplifiedZobristHashing + " has been repeated 3 times");
        return true;
      }
      return false;
    } else {
      this.stateCount.put(simplifiedZobristHashing, 1);
      return false;
    }
  }

  public HashMap<OptionType, String> getOptions() {
    return options;
  }

  public Board getBoard() {
    return this.gameState.getBoard();
  }

  public GameState getGameState() {
    return this.gameState;
  }

  public History getHistory() {
    return this.history;
  }

  /**
   * Returns whether the White player is controlled by an AI.
   *
   * @return true if the White player is an AI, false otherwise.
   */
  public boolean isWhiteAI() {
    return isWhiteAI;
  }

  /**
   * Returns whether the Black player is controlled by an AI.
   *
   * @return true if the Black player is an AI, false otherwise.
   */
  public boolean isBlackAI() {
    return isBlackAI;
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
   * Sets the exploration field to the given boolean. Use it in the solver.
   *
   * @param exploration boolean corresponding to the new rights of explorationAI.
   */
  public void setExploration(boolean exploration) {
    this.explorationAI = exploration;
  }

  /**
   * Indicates whether the AI is exploring (playing moves in its algorithm).
   *
   * @return True if it is exploring, False otherwise
   */
  public boolean isAIExploring() {
    return this.explorationAI;
  }

  /**
   * Plays the first AI move if White AI is activated. The other calls to AI will be done in {@link
   * Game#updateGameStateAfterMove}
   */
  public void startAI() {
    if (isWhiteAI && this.getGameState().isWhiteTurn()) {
      this.notifyObservers(EventType.AI_PLAYING);
      solverWhite.playAIMove(this);
    }
  }

  /**
   * Checks if the Game is in an end game phase. Used to know when to switch heuristics.
   *
   * @return true if wer're in an endgame (according to the chosen criterias)
   */
  public boolean isEndGamePhase() {
    int nbRequiredConditions = 4;
    int nbFilledConditions = 0;

    int halfNbPieces = 16;
    int nbPlayedMovesBeforeEndGame = 25;
    int nbPossibleMoveInEndGame = 25;

    // Queens are off the board
    if (getBoard().getBoardRep().queensOffTheBoard()) {
      nbFilledConditions++;
    }
    // Number of pieces remaining
    if (getBoard().getBoardRep().nbPiecesRemaining() <= halfNbPieces) {
      nbFilledConditions++;
    }
    // King activity
    if (getBoard().getBoardRep().areKingsActive()) {
      nbFilledConditions++;
    }
    // Number of played moves
    if (gameState.getFullTurn() >= nbPlayedMovesBeforeEndGame) {
      nbFilledConditions++;
    }
    // Number of possible Moves

    int nbMovesWhite;
    int nbMovesBlack;

    if (getBoard().getBoardRep() instanceof BitboardRepresentation) {
      nbMovesWhite =
          ((BitboardRepresentation) getBoard().getBoardRep()).getColorMoveBitboard(true).bitCount();
      nbMovesBlack =
          ((BitboardRepresentation) getBoard().getBoardRep())
              .getColorMoveBitboard(false)
              .bitCount();
    } else {
      nbMovesWhite = getBoard().getBoardRep().getAllAvailableMoves(true).size();
      nbMovesBlack = getBoard().getBoardRep().getAllAvailableMoves(false).size();
    }
    if (nbMovesWhite + nbMovesBlack <= nbPossibleMoveInEndGame) {
      nbFilledConditions++;
    }
    // Pawns progresses on the board
    if (getBoard().getBoardRep().pawnsHaveProgressed(this.gameState.isWhiteTurn())) {
      nbFilledConditions++;
    }

    return nbFilledConditions >= nbRequiredConditions;
  }

  /**
   * Adds an observer to the game and game state and immediately notifies a GAME_STARTED event.
   *
   * @param observer The observer to be added.
   */
  @Override
  public void addObserver(EventObserver observer) {
    DEBUG(LOGGER, "An observer have been attached to Game");
    super.addObserver(observer);
    if (gameState != null) {
      this.gameState.addObserver(observer);
    }
    this.notifyObserver(observer, EventType.GAME_STARTED);
  }

  /**
   * Adds an observer to the game and game state that listens for error events.
   *
   * @param observer The observer to be added.
   */
  @Override
  public void addErrorObserver(EventObserver observer) {
    DEBUG(LOGGER, "An error observer have been attached to Game");
    super.addErrorObserver(observer);
    if (gameState != null) {
      this.gameState.addErrorObserver(observer);
    }
  }

  /**
   * Creates a new instance of the Game class and stores it in the instance variable.
   *
   * @param isWhiteAI Whether the white player is an AI.
   * @param isBlackAI Whether the black player is an AI.
   * @param solverWhite The solver to use for White AI moves.
   * @param solverBlack The solver to use for Black AI moves.
   * @param options
   * @return The newly created instance of Game.
   */
  public static Game initialize(
      boolean isWhiteAI,
      boolean isBlackAI,
      Solver solverWhite,
      Solver solverBlack,
      Timer timer,
      HashMap<OptionType, String> options) {
    DEBUG(LOGGER, "Initializing Game...");
    instance =
        new Game(
            isWhiteAI,
            isBlackAI,
            solverWhite,
            solverBlack,
            new GameState(timer),
            new History(),
            options);
    BagOfCommands.getInstance().setModel(instance);
    if (timer != null) {
      timer.setCallback(instance::outOfTimeCallback);
      timer.start();
    }
    DEBUG(LOGGER, "Game initialized!");
    instance.notifyObservers(EventType.GAME_STARTED);
    return instance;
  }

  /**
   * Creates a new instance of the Game class and stores it in the instance variable.
   *
   * @param isWhiteAI Whether the white player is an AI.
   * @param isBlackAI Whether the black player is an AI.
   * @param solverWhite The solver to use for White AI moves.
   * @param solverBlack The solver to use for Black AI moves.
   * @param board The board state to use
   * @param options
   * @return The newly created instance of Game.
   */
  public static Game initialize(
      boolean isWhiteAI,
      boolean isBlackAI,
      Solver solverWhite,
      Solver solverBlack,
      Timer timer,
      FileBoard board,
      HashMap<OptionType, String> options) {
    DEBUG(LOGGER, "Initializing Game from given board...");
    instance =
        new Game(
            isWhiteAI,
            isBlackAI,
            solverWhite,
            solverBlack,
            new GameState(board, timer),
            new History(),
            options);
    BagOfCommands.getInstance().setModel(instance);
    if (timer != null) {
      timer.setCallback(instance::outOfTimeCallback);
      timer.start();
    }
    DEBUG(LOGGER, "Game initialized!");
    instance.notifyObservers(EventType.GAME_STARTED);
    return instance;
  }

  public void outOfTimeCallback() {
    DEBUG(LOGGER, "outOfTimeCallback called");
    this.gameState.playerOutOfTime(this.gameState.isWhiteTurn());
  }

  /**
   * Tries to play the given move on the game.
   *
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is not legal.
   */
  public void playMove(Move move) throws IllegalMoveException, InvalidPromoteFormatException {
    Position sourcePosition = new Position(move.source.getX(), move.source.getY());
    Position destPosition = new Position(move.dest.getX(), move.dest.getY());
    DEBUG(LOGGER, "Trying to play move [" + sourcePosition + ", " + destPosition + "]");

    if (!validatePieceOwnership(this.gameState, sourcePosition)) {
      throw new IllegalMoveException(move.toString());
    }
    validatePromotionMove(move);

    List<Move> availableMoves = this.gameState.getBoard().getAvailableMoves(sourcePosition);
    Optional<Move> classicalMove = move.isMoveClassical(availableMoves);

    if (classicalMove.isPresent()) {
      move = classicalMove.get();
      processClassicalMove(this.gameState, move);
    } else {
      processSpecialMove(this.gameState, move);
    }
    this.updateGameStateAfterMove(move, classicalMove.isPresent());
  }

  /**
   * Handles classical moves
   *
   * @param gameState the game state for which we want the move to occur
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  private void processClassicalMove(GameState gameState, Move move) throws IllegalMoveException {
    Color currentColor = gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK;
    if (gameState.getBoard().board.isCheckAfterMove(currentColor, move)) {
      DEBUG(LOGGER, "Move puts the king in check: " + move);
      throw new IllegalMoveException(move.toString());
    }

    gameState.getBoard().makeMove(move);
    DEBUG(LOGGER, "Move played!");
  }

  /**
   * Handles special moves: castling, en passant, double pawn push
   *
   * @param gameState the game state for which we want the move to occur
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  private void processSpecialMove(GameState gameState, Move move) throws IllegalMoveException {
    Position sourcePosition = move.source;
    Position destPosition = move.dest;
    boolean isSpecialMove = false;
    ColoredPiece coloredPiece =
        gameState.getBoard().board.getPieceAt(sourcePosition.getX(), sourcePosition.getY());

    // Check Castle
    if (isCastleMove(coloredPiece, sourcePosition, destPosition)) {
      boolean shortCastle = destPosition.getX() > sourcePosition.getX();
      Color color = gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK;
      if (gameState.getBoard().canCastle(color, shortCastle)) {
        gameState.getBoard().applyCastle(color, shortCastle);
        isSpecialMove = true;
      }
    }

    // Check en passant
    if (!isSpecialMove
        && gameState.getBoard().isLastMoveDoublePush
        && gameState
            .getBoard()
            .board
            .isEnPassant(
                gameState.getBoard().enPassantPos.getX(),
                gameState.getBoard().enPassantPos.getY(),
                move,
                gameState.getBoard().isWhite)) {
      if (gameState
          .getBoard()
          .board
          .isCheckAfterMove(gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK, move)) {
        DEBUG(LOGGER, "En passant puts the king in check!");
        throw new IllegalMoveException(move.toString());
      }
      isSpecialMove = true;
      gameState.getBoard().enPassantPos = null;
      gameState.getBoard().isEnPassantTake = true;
      move.piece = coloredPiece;
      move.isTake = true;
      gameState.getBoard().makeMove(move);
    }

    // Check double pawn push
    if (!isSpecialMove
        && gameState.getBoard().board.isDoublePushPossible(move, gameState.getBoard().isWhite)) {
      if (gameState
          .getBoard()
          .board
          .isCheckAfterMove(gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK, move)) {
        DEBUG(LOGGER, "Double push puts the king in check!");
        throw new IllegalMoveException(move.toString());
      }
      isSpecialMove = true;
      gameState.getBoard().enPassantPos =
          gameState.getBoard().isWhite
              ? new Position(move.dest.getX(), move.dest.getY() - 1)
              : new Position(move.dest.getX(), move.dest.getY() + 1);
      move.piece = coloredPiece;
      gameState.getBoard().makeMove(move);
      gameState.getBoard().isLastMoveDoublePush = true;
    }

    if (!isSpecialMove) {
      DEBUG(LOGGER, "Move was not a special move!");
      throw new IllegalMoveException(move.toString());
    }
  }

  /**
   * Return true if the piece located at sourcePosition is of the same color as the player that has
   * to play a move, false is not, and exception otherwise.
   *
   * @param gameState the game state for which we want to verify piece ownership
   * @param sourcePosition the position
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  private boolean validatePieceOwnership(GameState gameState, Position sourcePosition)
      throws IllegalMoveException {
    ColoredPiece pieceAtSource =
        gameState.getBoard().board.getPieceAt(sourcePosition.getX(), sourcePosition.getY());
    boolean isWhiteTurn = gameState.getBoard().isWhite;

    if ((pieceAtSource.color == Color.WHITE && !isWhiteTurn)
        || (pieceAtSource.color == Color.BLACK && isWhiteTurn)) {
      DEBUG(LOGGER, "Not a " + pieceAtSource.color + " piece at " + sourcePosition);
      return false;
    }
    return true;
  }

  /**
   * Checks if the given move is a promotion move and if is an instance of PromoteMove
   *
   * @param move The move to be validated.
   * @throws InvalidPromoteFormatException If the move is a promotion move but not of PromoteMove
   *     type.
   */
  private void validatePromotionMove(Move move) throws InvalidPromoteFormatException {
    if (this.isPromotionMove(move) && !(move instanceof PromoteMove)) {
      throw new InvalidPromoteFormatException();
    }
  }

  /**
   * Determines if the given move is a castle move.
   *
   * @param coloredPiece The piece being moved, expected to be a king for castling.
   * @param source The source position of the move.
   * @param dest The destination position of the move.
   * @return true if the move is a castle move, false otherwise.
   */
  private boolean isCastleMove(ColoredPiece coloredPiece, Position source, Position dest) {
    if (coloredPiece.piece != Piece.KING) {
      return false;
    }
    int deltaX = Math.abs(dest.getX() - source.getX());
    return deltaX == 2
        && ((source.getY() == 0 && dest.getY() == 0) || (source.getY() == 7 && dest.getY() == 7));
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
  private void updateGameStateAfterMove(Move move, boolean isSpecialMove) {

    if (this.gameState.getMoveTimer() != null) {
      this.gameState.getMoveTimer().stop();
    }

    if (this.gameState.getBoard().isWhite) {
      this.gameState.incrementsFullTurn();
    }

    this.gameState.switchPlayerTurn();
    this.gameState.getBoard().setPlayer(this.gameState.isWhiteTurn());
    if (!explorationAI) {
      if (isSpecialMove) {
        this.gameState.setSimplifiedZobristHashing(
            zobristHashing.updateSimplifiedHashFromBitboards(
                this.gameState.getSimplifiedZobristHashing(), getBoard(), move));
      } else {
        this.gameState.setSimplifiedZobristHashing(
            zobristHashing.generateSimplifiedHashFromBitboards(getBoard()));
      }

      DEBUG(LOGGER, "Checking threefold repetition...");
      boolean threefoldRepetition =
          this.addStateToCount(this.gameState.getSimplifiedZobristHashing());

      if (threefoldRepetition) {
        this.gameState.activateThreefold();
      }
    }

    DEBUG(LOGGER, "Checking phase of the game (endgame, middle game, etc.)...");
    if (isEndGamePhase()) {
      if (this.solverWhite != null) {
        // Set endgame heuristic only once and only if endgame phase
        if ((!(this.solverWhite.getAlgorithm() instanceof MonteCarloTreeSearch))
            && !(this.solverWhite.getHeuristic() instanceof EndGameHeuristic)) {
          this.solverWhite.setHeuristic(HeuristicType.ENDGAME);
        }
      }
      if (this.solverBlack != null) {
        if ((!(this.solverBlack.getAlgorithm() instanceof MonteCarloTreeSearch))
            && !(this.solverBlack.getHeuristic() instanceof EndGameHeuristic)) {
          this.solverBlack.setHeuristic(HeuristicType.ENDGAME);
        }
      }
    }
    DEBUG(LOGGER, "Checking game status...");
    this.gameState.checkGameStatus();

    this.history.addMove(new HistoryState(move, this.gameState.getCopy()));

    if (!explorationAI) {
      this.notifyObservers(EventType.MOVE_PLAYED);
    }

    if (this.gameState.getMoveTimer() != null && !this.gameState.isGameOver()) {
      this.gameState.getMoveTimer().start();
    }

    if (!explorationAI
        && !isOver()
        && ((this.gameState.getBoard().isWhite && isWhiteAI)
            || (!this.gameState.getBoard().isWhite && isBlackAI))) {

      if (VIEW_ON_OTHER_THREAD) {
        viewLock.lock();
        this.notifyObservers(EventType.AI_PLAYING);
        try {
          System.out.println("Waiting for View");
          workingView.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          viewLock.unlock();
        }
      } else {
        this.notifyObservers(EventType.AI_PLAYING);
      }
      if (this.gameState.getBoard().isWhite) {
        solverWhite.playAIMove(this);
      } else {
        solverBlack.playAIMove(this);
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
  public void saveGame(String path) throws FailedSaveException {
    boolean[] castlingRights = getBoard().getCastlingRights();
    String board =
        FENSaver.saveBoard(
            new FileBoard(
                this.getBoard().board,
                this.getBoard().isWhite,
                new FenHeader(
                    castlingRights[0],
                    castlingRights[1],
                    castlingRights[2],
                    castlingRights[3],
                    getBoard().enPassantPos,
                    getBoard().getNbMovesWithNoCaptureOrPawn() * 2,
                    getGameState().getFullTurn())));
    String gameStr = this.history.toAlgebraicString();

    String game = board + "\n" + gameStr;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      writer.write(game);
    } catch (IOException e) {
      DEBUG(LOGGER, "Error writing to file: " + e.getMessage());
      throw new FailedSaveException(path);
    }
    DEBUG(LOGGER, "Game saved to " + path);
    this.notifyObservers(EventType.GAME_SAVED);
  }

  /**
   * Retrieves the history of moves in the current game as a formatted string.
   *
   * @return A string representation of the game's movverify(model).previousState();e history.
   */
  public String getStringHistory() {
    return this.history.toString();
  }

  /** Restarts the game by resetting the game state and history. */
  public void restartGame() {
    DEBUG(LOGGER, "Restarting game");

    if (!gameState.isGameOver()) {
      if (this.gameState.isWhiteTurn()) {
        this.gameState.whiteResigns();
      } else {
        this.gameState.blackResigns();
      }
    }

    this.gameState.updateFrom(new GameState(this.gameState.getMoveTimer()));
    this.history = new History();

    this.history.addMove(
        new HistoryState(
            new Move(new Position(-1, -1), new Position(-1, -1)), this.gameState.getCopy()));

    this.stateCount.clear();
    this.gameState.setSimplifiedZobristHashing(
        zobristHashing.generateSimplifiedHashFromBitboards(this.gameState.getBoard()));
    this.addStateToCount(this.gameState.getSimplifiedZobristHashing());

    this.notifyObservers(EventType.GAME_RESTART);

    this.startAI();

    DEBUG(LOGGER, "Game restarted");
  }

  public boolean isOver() {
    return this.gameState.isGameOver();
  }

  /**
   * Initializes a new Game object from a list of moves.
   *
   * <p>The new game is initialized with the given AI settings, solver, and starting position.
   *
   * @param moves The moves to play in sequence.
   * @param isWhiteAI Whether the white player is an AI.
   * @param isBlackAI Whether the black player is an AI.
   * @param solverWhite The solver to use for White AI moves.
   * @param solverBlack The solver to use for Black AI moves.
   * @param timer The timer to use for the game.
   * @param options
   * @return A new Game object with the given moves played.
   * @throws IllegalMoveException If any of the given moves are illegal.
   */
  public static Game fromHistory(
      List<Move> moves,
      boolean isWhiteAI,
      boolean isBlackAI,
      Solver solverWhite,
      Solver solverBlack,
      Timer timer,
      HashMap<OptionType, String> options)
      throws IllegalMoveException {
    instance =
        new Game(
            isWhiteAI,
            isBlackAI,
            solverWhite,
            solverBlack,
            new GameState(timer),
            new History(),
            options);
    BagOfCommands.getInstance().setModel(instance);

    for (Move move : moves) {
      instance.playMove(move);
    }

    if (timer != null) {
      timer.setCallback(instance::outOfTimeCallback);
      timer.start();
    }

    instance.notifyObservers(EventType.GAME_STARTED);

    return instance;
  }

  /**
   * Moves to the previous move in history and updates the game state.
   *
   * <p>Throws a FailedUndoException if there is no previous move to undo. Notifies observers of the
   * move undo event.
   *
   * @throws FailedUndoException if no previous move exists.
   */
  public void previousState() throws FailedUndoException {
    this.gameState.undoRequestReset();

    Optional<HistoryNode> currentNode = this.history.getCurrentMove();
    if (!currentNode.isPresent()) {
      throw new FailedUndoException();
    }

    Optional<HistoryNode> previousNode = currentNode.get().getPrevious();
    if (!previousNode.isPresent()) {
      throw new FailedUndoException();
    }
    // update zobrist to avoid threefold
    if (!explorationAI) {
      long currBoardZobrist = this.gameState.getSimplifiedZobristHashing();
      if (stateCount.containsKey(currBoardZobrist)) {
        stateCount.put(currBoardZobrist, stateCount.get(currBoardZobrist) - 1);
      }
    }

    this.gameState.updateFrom(previousNode.get().getState().getGameState().getCopy());
    this.history.setCurrentMove(previousNode.get());
    DEBUG(LOGGER, "Move undo : change state and update Zobrist for threefold");
    this.notifyObservers(EventType.MOVE_UNDO);
  }

  /**
   * Moves to the next move in history and updates the game state.
   *
   * <p>Throws a FailedRedoException if there is no next move to redo. Notifies observers of the
   * move redo event.
   *
   * @throws FailedRedoException if no next move exists.
   */
  public void nextState() throws FailedRedoException {
    this.gameState.redoRequestReset();

    Optional<HistoryNode> currentNode = this.history.getCurrentMove();
    if (!currentNode.isPresent()) {
      throw new FailedRedoException();
    }

    Optional<HistoryNode> nextNode = currentNode.get().getNext();
    if (!nextNode.isPresent()) {
      throw new FailedRedoException();
    }

    this.gameState.updateFrom(nextNode.get().getState().getGameState().getCopy());
    this.history.setCurrentMove(nextNode.get());
    long currBoardZobrist = this.gameState.getSimplifiedZobristHashing();
    stateCount.put(currBoardZobrist, stateCount.getOrDefault(currBoardZobrist, 0) + 1);
    DEBUG(LOGGER, "Move redo : change state and update Zobrist for threefold");
    this.notifyObservers(EventType.MOVE_REDO);
  }

  /**
   * Returns a string representation of the game. Includes the ASCII representation of the board,
   * the time remaining (if timer is not null), and the color of the player to play.
   *
   * @return A string representation of the game.
   */
  public String getGameRepresentation() {
    char[][] board = this.gameState.getBoard().getAsciiRepresentation();
    StringBuilder sb = new StringBuilder();

    Timer timer = gameState.getMoveTimer();
    if (timer != null) {
      sb.append(TextGetter.getText("timeRemaining", timer.getTimeRemainingString()));
    }

    sb.append("\n");

    int size = board.length;

    for (int row = 0; row < size; row++) {
      sb.append(size - row).append(" | ");
      for (int col = 0; col < size; col++) {
        sb.append(board[row][col]).append(" ");
      }
      sb.append("\n");
    }

    sb.append("    "); // Offset for row numbers
    for (int i = 0; i < size; i++) {
      sb.append("-").append(" ");
    }
    sb.append("\n    ");
    for (char c = 'A'; c < 'A' + size; c++) {
      sb.append(c).append(" ");
    }
    sb.append("\n\n");

    if (!this.gameState.isGameOver()) {
      sb.append(
          TextGetter.getText(
              "toPlay",
              gameState.isWhiteTurn() ? TextGetter.getText("white") : TextGetter.getText("black")));
    } else {
      sb.append(TextGetter.getText("gameOver"));
    }

    sb.append("\n");

    return sb.toString();
  }

  /**
   * Determines if the given move is a pawn promotion move.
   *
   * @param move The move to be checked.
   * @return true if the move is a promotion move, false otherwise.
   */
  public boolean isPromotionMove(Move move) {
    if (this.gameState.getBoard().board.getPieceAt(move.source.getX(), move.source.getY()).piece
        != Piece.PAWN) {
      return false;
    }
    if (this.gameState.isWhiteTurn() && move.dest.getY() == 7) {
      return true;
    }
    if (!this.gameState.isWhiteTurn() && move.dest.getY() == 0) {
      return true;
    }
    return false;
  }

  /**
   * Tries to play the given move on the game for the game state in parameter
   *
   * @param gameState the game state for which we want the move to occur
   * @param move The move to be executed
   * @throws IllegalMoveException If the move is not legal
   */
  public void playMoveOtherGameState(GameState gameState, Move move)
      throws IllegalMoveException, InvalidPromoteFormatException {

    Position sourcePosition = new Position(move.source.getX(), move.source.getY());
    Position destPosition = new Position(move.dest.getX(), move.dest.getY());
    DEBUG(LOGGER, "Trying to play move [" + sourcePosition + ", " + destPosition + "]");

    if (!validatePieceOwnership(gameState, sourcePosition)) {
      throw new IllegalMoveException(move.toString());
    }
    validatePromotionMove(move);

    List<Move> availableMoves = gameState.getBoard().getAvailableMoves(sourcePosition);
    Optional<Move> classicalMove = move.isMoveClassical(availableMoves);

    if (classicalMove.isPresent()) {
      move = classicalMove.get();
      processClassicalMove(gameState, move);
    } else {
      processSpecialMove(gameState, move);
    }
    updateOtherGameStateAfterMove(gameState, move);
  }

  /**
   * Method used for MonteCarloTreeSearch simulation that processes gameState copies. Updates the
   * game state in parameter (supposed to be copy) after a move is played.
   *
   * <p>The provided game state is updated by:
   *
   * <ul>
   *   <li>Incrementing the full turn number if the move was made by white.
   *   <li>Switching the current player turn.
   *   <li>Updating the board player.
   *   <li>Checking the game status, which may end the game.
   * </ul>
   */
  private void updateOtherGameStateAfterMove(GameState gameState, Move move) {
    if (gameState.getMoveTimer() != null) {
      gameState.getMoveTimer().stop();
    }

    if (gameState.getBoard().isWhite) {
      gameState.incrementsFullTurn();
    }

    gameState.switchPlayerTurn();
    gameState.getBoard().setPlayer(gameState.isWhiteTurn());

    DEBUG(LOGGER, "Checking game status...");
    gameState.checkGameStatus();
  }

  /**
   * Retrieves the singleton instance of the Game.
   *
   * @return The single instance of Game.
   * @throws IllegalStateException If the Game has not been initialized.
   */
  public static Game getInstance() throws IllegalStateException {
    if (instance == null) {
      throw new IllegalStateException("Game has not been initialized");
    }
    return instance;
  }
}
