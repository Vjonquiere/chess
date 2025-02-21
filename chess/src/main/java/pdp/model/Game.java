package pdp.model;

import static pdp.utils.Logging.DEBUG;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
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
import pdp.model.ai.heuristics.EndGameHeuristic;
import pdp.model.board.*;
import pdp.model.history.History;
import pdp.model.history.HistoryNode;
import pdp.model.history.HistoryState;
import pdp.model.parsers.FenHeader;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.model.savers.BoardSaver;
import pdp.utils.Logging;
import pdp.utils.Position;
import pdp.utils.TextGetter;
import pdp.utils.Timer;

public class Game extends Subject {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static Game instance;
  private static ZobristHashing zobristHashing = new ZobristHashing();
  private GameState gameState;
  private Solver solver;
  private boolean isWhiteAI;
  private boolean isBlackAI;
  private boolean explorationAI;
  private History history;
  private HashMap<Long, Integer> stateCount;

  static {
    Logging.configureLogging(LOGGER);
  }

  private Game(
      boolean isWhiteAI, boolean isBlackAI, Solver solver, GameState gameState, History history) {
    this.isWhiteAI = isWhiteAI;
    this.isBlackAI = isBlackAI;
    this.explorationAI = false;
    this.solver = solver;
    this.gameState = gameState;
    this.history = history;
    this.history.addMove(
        new HistoryState(
            new Move(new Position(-1, -1), new Position(-1, -1)), this.gameState.getCopy()));
    this.stateCount = new HashMap<>();
    // this.gameState.setZobristHashing(zobristHashing.generateHashFromBitboards(this.gameState.getBoard()));
    this.gameState.setSimplifiedZobristHashing(
        zobristHashing.generateSimplifiedHashFromBitboards(this.gameState.getBoard()));
    this.addStateToCount(this.gameState.getSimplifiedZobristHashing());
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
   * Gets the solver used by the AI.
   *
   * @return the {@link Solver} instance used for AI decision-making.
   */
  public Solver getSolver() {
    return solver;
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
      solver.playAIMove(this);
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
   * @param solver The solver to be used for AI moves.
   * @return The newly created instance of Game.
   */
  public static Game initialize(boolean isWhiteAI, boolean isBlackAI, Solver solver, Timer timer) {
    DEBUG(LOGGER, "Initializing Game...");
    instance = new Game(isWhiteAI, isBlackAI, solver, new GameState(timer), new History());
    if (timer != null) {
      timer.setCallback(instance::outOfTimeCallback);
      timer.start();
    }
    DEBUG(LOGGER, "Game initialized!");
    return instance;
  }

  /**
   * Creates a new instance of the Game class and stores it in the instance variable.
   *
   * @param isWhiteAI Whether the white player is an AI.
   * @param isBlackAI Whether the black player is an AI.
   * @param solver The solver to be used for AI moves.
   * @param board The board state to use
   * @return The newly created instance of Game.
   */
  public static Game initialize(
      boolean isWhiteAI, boolean isBlackAI, Solver solver, Timer timer, FileBoard board) {
    DEBUG(LOGGER, "Initializing Game from given board...");
    instance = new Game(isWhiteAI, isBlackAI, solver, new GameState(board, timer), new History());
    if (timer != null) {
      timer.setCallback(instance::outOfTimeCallback);
      timer.start();
    }
    DEBUG(LOGGER, "Game initialized!");
    return instance;
  }

  public void outOfTimeCallback() {
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

    if (!validatePieceOwnership(sourcePosition)) {
      throw new IllegalMoveException(move.toString());
    }
    validatePromotionMove(move);

    List<Move> availableMoves = this.gameState.getBoard().getAvailableMoves(sourcePosition);
    Optional<Move> classicalMove = move.isMoveClassical(availableMoves);

    if (classicalMove.isPresent()) {
      move = classicalMove.get();
      processClassicalMove(move);
    } else {
      processSpecialMove(move);
    }
    this.updateGameStateAfterMove(move);
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

  private boolean validatePieceOwnership(Position sourcePosition) throws IllegalMoveException {
    ColoredPiece pieceAtSource =
        this.gameState.getBoard().board.getPieceAt(sourcePosition.getX(), sourcePosition.getY());
    boolean isWhiteTurn = this.gameState.getBoard().isWhite;
    if ((pieceAtSource.color == Color.WHITE && !isWhiteTurn)
        || (pieceAtSource.color == Color.BLACK && isWhiteTurn)) {
      DEBUG(
          LOGGER,
          "Not a " + pieceAtSource.color.toString() + "piece at " + sourcePosition.toString());
      return false;
    }
    return true;
  }

  /**
   * Handles classical moves
   *
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  private void processClassicalMove(Move move) throws IllegalMoveException {
    Color currentColor = this.gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK;
    if (this.gameState.getBoard().board.isCheckAfterMove(currentColor, move)) {
      DEBUG(LOGGER, "Move puts the king in check " + move.toString());
      throw new IllegalMoveException(move.toString());
    }

    this.gameState.getBoard().makeMove(move);
    DEBUG(LOGGER, "Move played!");
  }

  /**
   * Handles special moves: castling, en passant, double pawn push
   *
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  private void processSpecialMove(Move move) throws IllegalMoveException {
    Position sourcePosition = new Position(move.source.getX(), move.source.getY());
    Position destPosition = new Position(move.dest.getX(), move.dest.getY());
    boolean isSpecialMove = false;
    ColoredPiece coloredPiece =
        this.gameState.getBoard().board.getPieceAt(sourcePosition.getX(), sourcePosition.getY());

    // Check Castle
    if (isCastleMove(coloredPiece, sourcePosition, destPosition)) {
      boolean shortCastle = destPosition.getX() > sourcePosition.getX();
      Color color = this.gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK;
      if (this.gameState.getBoard().canCastle(color, shortCastle)) {
        this.gameState.getBoard().applyCastle(color, shortCastle);
        isSpecialMove = true;
      }
    }

    // Check en passant
    if (!isSpecialMove
        && this.gameState.getBoard().isLastMoveDoublePush
        && this.gameState
            .getBoard()
            .board
            .isEnPassant(
                this.gameState.getBoard().enPassantPos.getX(),
                this.gameState.getBoard().enPassantPos.getY(),
                move,
                this.gameState.getBoard().isWhite)) {
      if (this.gameState
          .getBoard()
          .board
          .isCheckAfterMove(this.gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK, move)) {
        DEBUG(LOGGER, "En passant puts the king in check!");
        throw new IllegalMoveException(move.toString());
      }
      isSpecialMove = true;
      this.gameState.getBoard().enPassantPos = null;
      this.gameState.getBoard().isEnPassantTake = true;
      move.piece = coloredPiece;
      move.isTake = true;
      this.gameState.getBoard().makeMove(move);
    }

    // Check double pawn push
    if (!isSpecialMove
        && this.gameState
            .getBoard()
            .board
            .isDoublePushPossible(move, this.gameState.getBoard().isWhite)) {
      if (this.gameState
          .getBoard()
          .board
          .isCheckAfterMove(this.gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK, move)) {
        DEBUG(LOGGER, "Double push puts the king in check!");
        throw new IllegalMoveException(move.toString());
      }
      isSpecialMove = true;
      this.gameState.getBoard().enPassantPos =
          this.gameState.getBoard().isWhite
              ? new Position(move.dest.getX(), move.dest.getY() - 1)
              : new Position(move.dest.getX(), move.dest.getY() + 1);
      move.piece = coloredPiece;
      this.gameState.getBoard().makeMove(move);
      this.gameState.getBoard().isLastMoveDoublePush = true;
    }

    if (!isSpecialMove) {
      DEBUG(LOGGER, "Move was not a special move!");
      throw new IllegalMoveException(move.toString());
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
  private void updateGameStateAfterMove(Move move) {

    if (this.gameState.getMoveTimer() != null) {
      this.gameState.getMoveTimer().stop();
    }

    if (this.gameState.getBoard().isWhite) {
      this.gameState.incrementsFullTurn();
    }

    this.gameState.switchPlayerTurn();
    this.gameState.getBoard().setPlayer(this.gameState.isWhiteTurn());
    if (!explorationAI) {
      this.gameState.setSimplifiedZobristHashing(
          zobristHashing.updateSimplifiedHashFromBitboards(
              this.gameState.getSimplifiedZobristHashing(), getBoard(), move));
      DEBUG(LOGGER, "Checking threefold repetition...");
      boolean threefoldRepetition =
          this.addStateToCount(this.gameState.getSimplifiedZobristHashing());
      if (threefoldRepetition) {
        this.gameState.activateThreefold();
      }
    }

    DEBUG(LOGGER, "Checking phase of the game (endgame, middle game, etc.)...");
    if (isEndGamePhase() && this.solver != null) {
      // Set endgame heuristic only once and only if endgame phase
      if (!(solver.getHeuristic() instanceof EndGameHeuristic)) {
        this.solver.setHeuristic(HeuristicType.ENDGAME);
      }
    }
    DEBUG(LOGGER, "Checking game status...");
    this.gameState.checkGameStatus();

    this.notifyObservers(EventType.MOVE_PLAYED);

    this.history.addMove(new HistoryState(move, this.gameState.getCopy()));

    if (this.gameState.getMoveTimer() != null && !this.gameState.isGameOver()) {
      this.gameState.getMoveTimer().start();
    }

    if (!explorationAI
        && !isOver()
        && ((this.gameState.getBoard().isWhite && isWhiteAI)
            || (!this.gameState.getBoard().isWhite && isBlackAI))) {
      this.notifyObservers(EventType.AI_PLAYING);
      solver.playAIMove(this);
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
        BoardSaver.saveBoard(
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

  public void resetGame() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
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
   * @param solver The solver to use for AI moves.
   * @param timer The timer to use for the game.
   * @return A new Game object with the given moves played.
   * @throws IllegalMoveException If any of the given moves are illegal.
   */
  public static Game fromHistory(
      List<Move> moves, boolean isWhiteAI, boolean isBlackAI, Solver solver, Timer timer)
      throws IllegalMoveException {
    instance = new Game(isWhiteAI, isBlackAI, solver, new GameState(timer), new History());

    for (Move move : moves) {
      instance.playMove(move);
    }

    if (timer != null) {
      timer.setCallback(instance::outOfTimeCallback);
      timer.start();
    }

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
    Optional<HistoryNode> currentNode = this.history.getCurrentMove();
    if (!currentNode.isPresent()) {
      throw new FailedUndoException();
    }

    Optional<HistoryNode> previousNode = currentNode.get().getPrevious();
    if (!previousNode.isPresent()) {
      throw new FailedUndoException();
    }
    // update zobrist to avoid threefold
    long currBoardZobrist = this.gameState.getZobristHashing();
    if (stateCount.containsKey(currBoardZobrist)) {
      stateCount.put(currBoardZobrist, stateCount.get(currBoardZobrist) - 1);
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
    long currBoardZobrist = this.gameState.getZobristHashing();
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

  public static Game getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Game has not been initialized");
    }
    return instance;
  }
}
