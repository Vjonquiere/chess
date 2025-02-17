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
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPromoteFormatException;
import pdp.model.ai.Solver;
import pdp.model.board.*;
import pdp.model.history.History;
import pdp.model.history.HistoryNode;
import pdp.model.history.HistoryState;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.model.savers.BoardSaver;
import pdp.utils.Logging;
import pdp.utils.Position;
import pdp.utils.TextGetter;

public class Game extends Subject {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static Game instance;
  private static ZobristHashing zobristHashing = new ZobristHashing();
  private GameState gameState;
  private Solver solver;
  private boolean isWhiteAI;
  private boolean isBlackAI;
  private History history;
  private HashMap<Long, Integer> stateCount;

  private Game(
      boolean isWhiteAI, boolean isBlackAI, Solver solver, GameState gameState, History history) {
    Logging.configureLogging(LOGGER);
    this.isWhiteAI = isWhiteAI;
    this.isBlackAI = isBlackAI;
    this.solver = solver;
    this.gameState = gameState;
    this.history = history;
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
   * Checks if the Game is in an end game phase. Used to know when to switch heuristics.
   *
   * @return true if wer're in an endgame (according to the chosen criterias)
   */
  public boolean isEndGamePhase() {
    int nbRequiredConditions = 3;
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
    Optional<HistoryNode> previousNode = history.getPrevious();
    if (previousNode.isPresent()) {
      HistoryNode node = previousNode.get();
      if (node.getState().getFullTurn() >= nbPlayedMovesBeforeEndGame) {
        nbFilledConditions++;
      }
    }
    // Number of possible Moves
    int nbMovesWhite = getBoard().getBoardRep().getAllAvailableMoves(true).size();
    int nbMovesBlack = getBoard().getBoardRep().getAllAvailableMoves(false).size();
    if (nbMovesWhite + nbMovesBlack <= nbPossibleMoveInEndGame) {
      nbFilledConditions++;
    }

    // Pawns progresses on the board
    if (getBoard().getBoardRep().pawnsHaveProgressed()) {
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
    instance = new Game(isWhiteAI, isBlackAI, solver, new GameState(), new History());
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
    instance = new Game(isWhiteAI, isBlackAI, solver, new GameState(board), new History());
    DEBUG(LOGGER, "Game initialized!");
    return instance;
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

    if (this.gameState.getBoard().isWhite) {
      this.gameState.incrementsFullTurn();
    }
    this.history.addMove(
        new HistoryState(move, this.gameState.getFullTurn(), this.gameState.getBoard().isWhite));

    this.gameState.switchPlayerTurn();
    this.gameState.getBoard().setPlayer(this.gameState.isWhiteTurn());
    this.gameState.setSimplifiedZobristHashing(
        zobristHashing.updateSimplifiedHashFromBitboards(
            this.gameState.getSimplifiedZobristHashing(), getBoard(), move));
    DEBUG(LOGGER, "Checking threefold repetition...");
    boolean threefoldRepetition =
        this.addStateToCount(this.gameState.getSimplifiedZobristHashing());
    if (threefoldRepetition) {
      this.gameState.activateThreefold();
    }
    DEBUG(LOGGER, "Checking game status...");
    this.gameState.checkGameStatus();
    this.notifyObservers(EventType.MOVE_PLAYED);
  }

  public void saveGame(String path) {
    String board =
        BoardSaver.saveBoard(new FileBoard(this.getBoard().board, this.getBoard().isWhite));
    String gameStr = this.history.toAlgebraicString();

    String game = board + "\n" + gameStr;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      writer.write(game);
    } catch (IOException e) {
      System.err.println("Error writing to file: " + e.getMessage());
    }
  }

  /**
   * Retrieves the history of moves in the current game as a formatted string.
   *
   * @return A string representation of the game's move history.
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
   * @return A new Game object with the given moves played.
   * @throws IllegalMoveException If any of the given moves are illegal.
   */
  public static Game fromHistory(
      List<Move> moves, boolean isWhiteAI, boolean isBlackAI, Solver solver)
      throws IllegalMoveException {
    Game game = new Game(isWhiteAI, isBlackAI, solver, new GameState(), new History());

    for (Move move : moves) {
      game.playMove(move);
    }

    instance = game;
    return instance;
  }

  public void previousState() {
    // TODO: restore previous state from history
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
      instance = new Game(false, false, null, new GameState(), new History());
    }
    return instance;
  }
}
