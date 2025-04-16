package pdp.model;

import static pdp.utils.Logging.debug;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import pdp.events.EventType;
import pdp.events.Subject;
import pdp.exceptions.FailedRedoException;
import pdp.exceptions.FailedUndoException;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPromoteFormatException;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.CastlingMove;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.board.ZobristHashing;
import pdp.model.history.History;
import pdp.model.history.HistoryNode;
import pdp.model.history.HistoryState;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Class containing common methods for GameAI and Game. */
public abstract class GameAbstract extends Subject {
  /** Three-fold repetition, in games against external AI, it turns into a five-fold repetition. */
  private static int nFoldRepetition = 3;

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(GameAbstract.class.getName());

  /** Zobrist to create the hash. */
  private ZobristHashing zobristHashing = new ZobristHashing();

  /** Game state corresponding to this game. */
  private final GameState gameState;

  /** Number of times a board has been encountered, used for threefold. */
  private final Map<Long, Integer> stateCount;

  /** History of the game, used for undo and redo. */
  private final History history;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Retrieves the number of times the same board is encountered before ending the game by a draw.
   *
   * @return number of repetitions for the threefold rule
   */
  public static int getThreeFoldLimit() {
    return nFoldRepetition;
  }

  /**
   * Sets the number of repetition for the three-fold rule. Usually 3, or 5 for games against
   * Stockfish.
   *
   * @param limit number of repetitions.
   */
  public static void setThreeFoldLimit(final int limit) {
    nFoldRepetition = limit;
  }

  /**
   * Initializes the private fields of the Game abstract.
   *
   * @param gameState Current game state
   * @param history History of the game
   * @param stateCount Current state count
   */
  public GameAbstract(
      final GameState gameState, final History history, final Map<Long, Integer> stateCount) {
    super();
    this.gameState = gameState;
    this.history = history;
    this.stateCount = stateCount;
  }

  /**
   * Initializes the private fields of the Game abstract.
   *
   * @param gameState Current game state
   * @param history History of the game
   * @param stateCount Current state count
   * @param zobristHashing instance of zobrist to avoid too many instances.
   */
  public GameAbstract(
      final GameState gameState,
      final History history,
      final HashMap<Long, Integer> stateCount,
      final ZobristHashing zobristHashing) {
    super();
    this.gameState = gameState;
    this.history = history;
    this.stateCount = stateCount;
    this.zobristHashing = zobristHashing;
  }

  /**
   * Plays a move. Only plays it if it is a legal move. Implemented in the subclasses.
   *
   * @param move Move to play
   */
  public abstract void playMove(Move move);

  /**
   * Add a state to the count of seen states. If the state has been seen 3 times, returns true.
   *
   * @param hash the simplified Zobrist hashing of the state
   * @return true if the state has been seen 3 times, false otherwise
   */
  protected boolean addStateToCount(final long hash) {
    debug(LOGGER, "Adding hash [" + hash + "] to count");
    if (this.stateCount.containsKey(hash)) {
      this.stateCount.put(hash, this.stateCount.get(hash) + 1);

      if (this.stateCount.get(hash) == nFoldRepetition) {
        debug(LOGGER, "State with hash " + hash + " has been repeated 3 times");
        return true;
      }
    } else {
      this.stateCount.put(hash, 1);
    }
    return false;
  }

  /**
   * Retrieves the board of the current game state.
   *
   * @return Board of the GameState
   */
  public BoardRepresentation getBoard() {
    return this.gameState.getBoard();
  }

  /**
   * Retrieves the instance of ZobristHashing used to create the hash.
   *
   * @return field zobristHashing
   */
  public ZobristHashing getZobristHasher() {
    return zobristHashing;
  }

  /**
   * Retrieves the current Game state of the game.
   *
   * @return field gameState
   */
  public GameState getGameState() {
    return this.gameState;
  }

  /**
   * Retrieves the history of the game.
   *
   * @return field history
   */
  public History getHistory() {
    return this.history;
  }

  /**
   * Retrieves the map containing the number of times the different boards have been encountered.
   *
   * @return field stateCount
   */
  public Map<Long, Integer> getStateCount() {
    return stateCount;
  }

  /**
   * Handles classical moves.
   *
   * @param gameState the game state for which we want the move to occur
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  protected void processMove(final GameState gameState, final Move move) {
    final Color currentColor = gameState.isWhiteTurn() ? Color.WHITE : Color.BLACK;

    final Position sourcePosition = move.getSource();
    final Position destPosition = move.getDest();
    final ColoredPiece coloredPiece =
        gameState.getBoard().getPieceAt(sourcePosition.x(), sourcePosition.y());

    if (gameState.getBoard().isCheckAfterMove(currentColor, move)) {
      debug(LOGGER, "Move puts the king in check: " + move);
      throw new IllegalMoveException(move.toString());
    }

    if (move instanceof CastlingMove) {
      final CastlingMove castlingMove = (CastlingMove) move;
      final Color color = gameState.isWhiteTurn() ? Color.WHITE : Color.BLACK;
      if (gameState.getBoard().canCastle(color, castlingMove.isShortCastle())) {
        gameState.getBoard().applyCastle(color, castlingMove.isShortCastle());
      } else {
        debug(LOGGER, "Castle is not possible: " + move);
        throw new IllegalMoveException(move.toString());
      }
    } else {
      gameState.getBoard().makeMove(move);
      debug(LOGGER, "Move played!");
    }

    if (coloredPiece.getPiece() == Piece.PAWN
        && Math.abs(destPosition.y() - sourcePosition.y()) == 2) {
      final Position enPassantPos =
          new Position(destPosition.x(), (sourcePosition.y() + destPosition.y()) / 2);
      gameState.getBoard().setEnPassantPos(enPassantPos);
      gameState.getBoard().setLastMoveDoublePush(true);
    } else {
      gameState.getBoard().setLastMoveDoublePush(false);
      gameState.getBoard().setEnPassantPos(null);
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
  protected boolean validatePieceOwnership(
      final GameState gameState, final Position sourcePosition) {
    return gameState.getBoard().validatePieceOwnership(gameState.isWhiteTurn(), sourcePosition);
  }

  /**
   * Checks if the given move is a promotion move and if is an instance of PromoteMove.
   *
   * @param move The move to be validated.
   * @throws InvalidPromoteFormatException If the move is a promotion move but not of PromoteMove
   *     type.
   */
  protected void validatePromotionMove(final Move move) {
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
  protected boolean isCastleMove(
      final ColoredPiece coloredPiece, final Position source, final Position dest) {
    return getBoard().isCastleMove(coloredPiece, source, dest);
  }

  /**
   * Checks if the Game is in an end game phase. Used to know when to switch heuristics.
   *
   * @return true if we're in an endgame (according to the chosen criteria)
   */
  public boolean isEndGamePhase() {
    return getBoard().isEndGamePhase(getGameState().getFullTurn(), getGameState().isWhiteTurn());
  }

  /**
   * Retrieves a boolean to indicate whether the game is over.
   *
   * @return true if the game is over, false otherwise.
   */
  public boolean isOver() {
    return this.gameState.isGameOver();
  }

  /**
   * Moves to the previous move in history and updates the game state.
   *
   * <p>Throws a FailedUndoException if there is no previous move to undo. Notifies observers of the
   * move undo event.
   *
   * @throws FailedUndoException if no previous move exists.
   */
  public void previousState() {
    this.gameState.undoRequestReset();

    final Optional<HistoryNode> currentNode = this.history.getCurrentMove();
    if (currentNode.isEmpty()) {
      throw new FailedUndoException();
    }

    final Optional<HistoryNode> previousNode = currentNode.get().getPrevious();
    if (previousNode.isEmpty()) {
      throw new FailedUndoException();
    }
    // update zobrist to avoid threefold
    final long currBoardZobrist = this.gameState.getSimplifiedZobristHashing();
    if (stateCount.containsKey(currBoardZobrist)) {
      stateCount.put(currBoardZobrist, stateCount.get(currBoardZobrist) - 1);
    }

    this.gameState.updateFrom(previousNode.get().getState().getGameState().getCopy());
    this.history.setCurrentMove(previousNode.get());
    debug(LOGGER, "Move undo : change state and update Zobrist for threefold");
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
  public void nextState() {
    this.gameState.redoRequestReset();

    final Optional<HistoryNode> currentNode = this.history.getCurrentMove();
    if (currentNode.isEmpty()) {
      throw new FailedRedoException();
    }

    final Optional<HistoryNode> nextNode = currentNode.get().getNext();
    if (nextNode.isEmpty()) {
      throw new FailedRedoException();
    }

    this.gameState.updateFrom(nextNode.get().getState().getGameState().getCopy());
    this.history.setCurrentMove(nextNode.get());
    final long currBoardZobrist = this.gameState.getSimplifiedZobristHashing();
    stateCount.put(currBoardZobrist, stateCount.getOrDefault(currBoardZobrist, 0) + 1);
    debug(LOGGER, "Move redo : change state and update Zobrist for threefold");
    this.notifyObservers(EventType.MOVE_REDO);
  }

  /**
   * Determines if the given move is a pawn promotion move.
   *
   * @param move The move to be checked.
   * @return true if the move is a promotion move, false otherwise.
   */
  public boolean isPromotionMove(final Move move) {
    return getBoard()
        .isPromotionMove(
            move.getSource().x(),
            move.getSource().y(),
            move.getDest().x(),
            move.getDest().y(),
            getGameState().isWhiteTurn());
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
  protected void updateGameStateAfterMove(final Move move) {
    if (this.getGameState().isWhiteTurn()) {
      this.getGameState().incrementsFullTurn();
    }

    this.getGameState().switchPlayerTurn();
    if (move instanceof CastlingMove || move instanceof PromoteMove) {
      this.getGameState()
          .setSimplifiedZobristHashing(
              this.getZobristHasher().generateSimplifiedHashFromBitboards(getBoard()));
    } else {
      this.getGameState()
          .setSimplifiedZobristHashing(
              this.getZobristHasher()
                  .updateSimplifiedHashFromBitboards(
                      this.getGameState().getSimplifiedZobristHashing(), getBoard(), move));
    }

    debug(LOGGER, "Checking threefold repetition...");
    final boolean threefoldRep =
        this.addStateToCount(this.getGameState().getSimplifiedZobristHashing());

    if (threefoldRep) {
      this.getGameState().activateThreefold();
    }

    debug(LOGGER, "Checking game status...");
    this.getGameState().checkGameStatus();

    this.getHistory().addMove(new HistoryState(move, this.getGameState().getCopy()));
  }
}
