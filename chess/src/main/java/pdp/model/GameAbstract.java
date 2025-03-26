package pdp.model;

import static pdp.utils.Logging.debug;

import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;
import pdp.events.EventType;
import pdp.events.Subject;
import pdp.exceptions.FailedRedoException;
import pdp.exceptions.FailedUndoException;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPromoteFormatException;
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.board.ZobristHashing;
import pdp.model.history.History;
import pdp.model.history.HistoryNode;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Class containing common methods for GameAI and Game. */
public abstract class GameAbstract extends Subject {
  private static int THREE_FOLD_REPETITION = 3;
  private static final Logger LOGGER = Logger.getLogger(GameAbstract.class.getName());
  private ZobristHashing zobristHashing = new ZobristHashing();
  private GameState gameState;
  private HashMap<Long, Integer> stateCount;
  private History history;

  static {
    Logging.configureLogging(LOGGER);
  }

  public static int getThreeFoldLimit() {
    return THREE_FOLD_REPETITION;
  }

  public static void setThreeFoldLimit(int limit) {
    THREE_FOLD_REPETITION = limit;
  }

  /**
   * Initializes the private fields of the Game abstract.
   *
   * @param gameState Current game state
   * @param history History of the game
   * @param stateCount Current state count
   */
  public GameAbstract(GameState gameState, History history, HashMap<Long, Integer> stateCount) {
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
      GameState gameState,
      History history,
      HashMap<Long, Integer> stateCount,
      ZobristHashing zobristHashing) {
    this.gameState = gameState;
    this.history = history;
    this.stateCount = stateCount;
    this.zobristHashing = zobristHashing;
  }

  public abstract void playMove(Move move);

  /**
   * Add a state to the count of seen states. If the state has been seen 3 times, returns true.
   *
   * @param simplifiedZobristHashing the simplified Zobrist hashing of the state
   * @return true if the state has been seen 3 times, false otherwise
   */
  protected boolean addStateToCount(long simplifiedZobristHashing) {
    debug(LOGGER, "Adding hash [" + simplifiedZobristHashing + "] to count");
    if (this.stateCount.containsKey(simplifiedZobristHashing)) {
      this.stateCount.put(
          simplifiedZobristHashing, this.stateCount.get(simplifiedZobristHashing) + 1);

      if (this.stateCount.get(simplifiedZobristHashing) == Game.nFoldRepetition) {
        debug(LOGGER, "State with hash " + simplifiedZobristHashing + " has been repeated 3 times");
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

  public ZobristHashing getZobristHasher() {
    return zobristHashing;
  }

  public GameState getGameState() {
    return this.gameState;
  }

  public History getHistory() {
    return this.history;
  }

  public HashMap<Long, Integer> getStateCount() {
    return stateCount;
  }

  /**
   * Handles classical moves.
   *
   * @param gameState the game state for which we want the move to occur
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  protected void processClassicalMove(GameState gameState, Move move) throws IllegalMoveException {
    Color currentColor = gameState.isWhiteTurn() ? Color.WHITE : Color.BLACK;
    if (gameState.getBoard().getBoardRep().isCheckAfterMove(currentColor, move)) {
      debug(LOGGER, "Move puts the king in check: " + move);
      throw new IllegalMoveException(move.toString());
    }

    gameState.getBoard().makeMove(move);
    debug(LOGGER, "Move played!");
  }

  /**
   * Handles special moves: castling, en passant, double pawn push.
   *
   * @param gameState the game state for which we want the move to occur
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  protected void processSpecialMove(GameState gameState, Move move) throws IllegalMoveException {
    Position sourcePosition = move.getSource();
    Position destPosition = move.getDest();
    boolean isSpecialMove = false;
    ColoredPiece coloredPiece =
        gameState.getBoard().getBoardRep().getPieceAt(sourcePosition.x(), sourcePosition.y());

    // Check Castle
    if (isCastleMove(coloredPiece, sourcePosition, destPosition)) {
      boolean shortCastle = destPosition.x() > sourcePosition.x();
      Color color = gameState.isWhiteTurn() ? Color.WHITE : Color.BLACK;
      if (gameState.getBoard().canCastle(color, shortCastle)) {
        gameState.getBoard().applyCastle(color, shortCastle);
        isSpecialMove = true;
      }
    }

    // Check en passant
    if (!isSpecialMove
        && gameState.getBoard().isLastMoveDoublePush()
        && gameState
            .getBoard()
            .getBoardRep()
            .isEnPassant(
                gameState.getBoard().getEnPassantPos().x(),
                gameState.getBoard().getEnPassantPos().y(),
                move,
                gameState.isWhiteTurn())) {
      if (gameState
          .getBoard()
          .getBoardRep()
          .isCheckAfterMove(gameState.isWhiteTurn() ? Color.WHITE : Color.BLACK, move)) {
        debug(LOGGER, "En passant puts the king in check!");
        throw new IllegalMoveException(move.toString());
      }
      isSpecialMove = true;
      gameState.getBoard().setEnPassantPos(null);
      gameState.getBoard().setEnPassantTake(true);
      move.setPiece(coloredPiece);
      move.setTake(true);
      move.setPieceTaken(
          new ColoredPiece(Piece.PAWN, !gameState.isWhiteTurn() ? Color.BLACK : Color.WHITE));
      gameState.getBoard().makeMove(move);
    }

    gameState.getBoard().setLastMoveDoublePush(false);
    gameState.getBoard().setEnPassantPos(null);
    // Check double pawn push
    if (!isSpecialMove
        && gameState.getBoard().getBoardRep().isDoublePushPossible(move, gameState.isWhiteTurn())) {
      if (gameState
          .getBoard()
          .getBoardRep()
          .isCheckAfterMove(gameState.isWhiteTurn() ? Color.WHITE : Color.BLACK, move)) {
        debug(LOGGER, "Double push puts the king in check!");
        throw new IllegalMoveException(move.toString());
      }
      isSpecialMove = true;
      gameState
          .getBoard()
          .setEnPassantPos(
              gameState.isWhiteTurn()
                  ? new Position(move.getDest().x(), move.getDest().y() - 1)
                  : new Position(move.getDest().x(), move.getDest().y() + 1));
      move.setPiece(coloredPiece);
      gameState.getBoard().makeMove(move);
      gameState.getBoard().setLastMoveDoublePush(true);
    }

    if (!isSpecialMove) {
      debug(LOGGER, "Move was not a special move!");
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
  protected boolean validatePieceOwnership(GameState gameState, Position sourcePosition) {
    return gameState
        .getBoard()
        .getBoardRep()
        .validatePieceOwnership(gameState.isWhiteTurn(), sourcePosition);
  }

  /**
   * Checks if the given move is a promotion move and if is an instance of PromoteMove.
   *
   * @param move The move to be validated.
   * @throws InvalidPromoteFormatException If the move is a promotion move but not of PromoteMove
   *     type.
   */
  protected void validatePromotionMove(Move move) throws InvalidPromoteFormatException {
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
  protected boolean isCastleMove(ColoredPiece coloredPiece, Position source, Position dest) {
    return getBoard().getBoardRep().isCastleMove(coloredPiece, source, dest);
  }

  /**
   * Checks if the Game is in an end game phase. Used to know when to switch heuristics.
   *
   * @return true if we're in an endgame (according to the chosen criterias)
   */
  public boolean isEndGamePhase() {
    return getBoard()
        .getBoardRep()
        .isEndGamePhase(getGameState().getFullTurn(), getGameState().isWhiteTurn());
  }

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
    long currBoardZobrist = this.gameState.getSimplifiedZobristHashing();
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
    debug(LOGGER, "Move redo : change state and update Zobrist for threefold");
    this.notifyObservers(EventType.MOVE_REDO);
  }

  /**
   * Determines if the given move is a pawn promotion move.
   *
   * @param move The move to be checked.
   * @return true if the move is a promotion move, false otherwise.
   */
  public boolean isPromotionMove(Move move) {
    return getBoard()
        .getBoardRep()
        .isPromotionMove(
            move.getSource().x(),
            move.getSource().y(),
            move.getDest().x(),
            move.getDest().y(),
            getGameState().isWhiteTurn());
  }
}
