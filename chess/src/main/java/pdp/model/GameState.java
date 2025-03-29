package pdp.model;

import static pdp.utils.Logging.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pdp.events.EventType;
import pdp.events.Subject;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.BoardRepresentation;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.utils.Logging;
import pdp.utils.Timer;

/** State of the game. Can be observed by an observer. */
public class GameState extends Subject {

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(GameState.class.getName());

  /** Number of move for the fifty move rule ( can be more than fifty when playing in UCI). */
  private static int nMoveRule = 50;

  /** Chess board of the current state. */
  private BoardRepresentation board;

  /** Timer for the blitz. */
  private Timer moveTimer;

  /** Boolean to indicates whether the white player wants to draw or not. */
  private boolean whiteWantsToDraw;

  /** Boolean to indicates whether the black player wants to draw or not. */
  private boolean blackWantsToDraw;

  /** Boolean to indicates whether the white player wants to resign or not. */
  private boolean whiteHasResigned;

  /** Boolean to indicates whether the black player wants to resign or not. */
  private boolean blackHasResigned;

  /** Turn when the undo request was made. */
  private int undoRequestTurnNb = -1;

  /** Turn when the redo request was made. */
  private int redoRequestTurnNb = -1;

  /** Boolean to indicates whether the white player has lost on time or not. */
  private boolean whiteLosesOnTime;

  /** Boolean to indicates whether the black player has lost on time or not. */
  private boolean blackLosesOnTime;

  /** Boolean to indicates whether the game is over or not. */
  private boolean gameOver;

  /** Boolean to indicates whether a threefold repetition has happened or not. */
  private boolean threefoldRepetition;

  /** Number of full turn since the start of the game. */
  private int fullTurnNumber;

  /** Zobrist hash corresponding to the current board. */
  private long zobristHashing;

  /** Simplified Zobrist hash corresponding to the current board. */
  private long simplifiedZobristHashing;

  /** List containing hint integers corresponding to the best move. */
  private List<Integer> hintIntegers = new ArrayList<>();

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Retrieves the maximum number of moves for the fifty move rule.
   *
   * @return field nMoveRule
   */
  public static int getFiftyMoveLimit() {
    return nMoveRule;
  }

  /**
   * Sets the value of nMove rule to limit.
   *
   * @param limit number of moves for the nMoveRule
   */
  public static void setFiftyMoveLimit(final int limit) {
    nMoveRule = limit;
  }

  /** Creates a game state with default parameters. By default, blitz mode is not on */
  public GameState() {
    this(new BitboardRepresentation(), null, 0);
  }

  /**
   * Creates a game state with default parameter and the blitz.
   *
   * @param timer Timer for the blitz
   */
  public GameState(final Timer timer) {
    this(new BitboardRepresentation(), timer, 0);
  }

  /**
   * Create a new GameState from a given board.
   *
   * @param board The FileBoard to use
   */
  public GameState(final FileBoard board) {
    this(
        new BitboardRepresentation(board),
        null,
        board.header() != null ? board.header().playedMoves() : 0);
  }

  /**
   * Create a new GameState from a given board with a timer.
   *
   * @param board The FileBoard to use
   * @param timer The timer to use
   */
  public GameState(final FileBoard board, final Timer timer) {
    this(
        new BitboardRepresentation(board),
        timer,
        board.header() != null ? board.header().playedMoves() : 0);
  }

  /**
   * Private constructor used by the public ones to create a GameState.
   *
   * @param board The board to use
   * @param moveTimer The timer to use
   * @param fullTurnNumber The number of full turns
   */
  private GameState(
      final BitboardRepresentation board, final Timer moveTimer, final int fullTurnNumber) {
    super();
    this.gameOver = false;
    this.board = board;
    this.moveTimer = moveTimer;
    this.fullTurnNumber = fullTurnNumber;
  }

  /**
   * Retrieves a boolean corresponding to the color of the current player.
   *
   * @return true if the player is white, false if he is white
   */
  public boolean isWhiteTurn() {
    return this.board.getPlayer();
  }

  /** Changes the color of the current player. */
  public void switchPlayerTurn() {
    this.board.setPlayer(!this.board.getPlayer());
  }

  /**
   * Retrieves the current board.
   *
   * @return current board
   */
  public BoardRepresentation getBoard() {
    return board;
  }

  /**
   * Retrieves the current timer.
   *
   * @return current timer
   */
  public Timer getMoveTimer() {
    return this.moveTimer;
  }

  /**
   * Retrieves the zobrist hash of the game state.
   *
   * @return current zobrist hashing
   */
  public long getZobristHashing() {
    return this.zobristHashing;
  }

  /**
   * Retrieves the simplified zobrist of the game state.
   *
   * @return current simplified zobrist
   */
  public long getSimplifiedZobristHashing() {
    return this.simplifiedZobristHashing;
  }

  /**
   * Retrieves the number of turns played since the beginning.
   *
   * @return the number of full turns
   */
  public int getFullTurn() {
    return this.fullTurnNumber;
  }

  /** Adds one to the full turn counter. */
  public void incrementsFullTurn() {
    this.fullTurnNumber += 1;
  }

  /**
   * Retrieves the turn number when the undo request was made.
   *
   * @return the turn number of the undo request
   */
  public int getUndoRequestTurnNumber() {
    return this.undoRequestTurnNb;
  }

  /**
   * Retrieves the turn number when the redo request was made.
   *
   * @return the turn number of the redo request
   */
  public int getRedoRequestTurnNumber() {
    return this.redoRequestTurnNb;
  }

  /**
   * Retrieves the list of hint integers.
   *
   * @return a list of integers representing the hints
   */
  public List<Integer> getHintIntegers() {
    return this.hintIntegers;
  }

  /**
   * Requests to undo the last move by recording the current turn number. Notifies observers about
   * the undo proposal based on the player's turn.
   */
  public void undoRequest() {
    this.undoRequestTurnNb = this.getFullTurn();
    if (this.isWhiteTurn()) {
      notifyObservers(EventType.WHITE_UNDO_PROPOSAL);
    } else {
      notifyObservers(EventType.BLACK_UNDO_PROPOSAL);
    }
  }

  /**
   * Resets the undo request by setting the recorded turn number to -1. This indicates that there is
   * no more active undo request.
   */
  public void undoRequestReset() {
    this.undoRequestTurnNb = -1;
  }

  /**
   * Requests to redo the last undone move by recording the current turn number. Notifies observers
   * about the redo proposal based on the player's turn.
   */
  public void redoRequest() {
    this.redoRequestTurnNb = this.getFullTurn();
    if (this.isWhiteTurn()) {
      notifyObservers(EventType.WHITE_REDO_PROPOSAL);
    } else {
      notifyObservers(EventType.BLACK_REDO_PROPOSAL);
    }
  }

  /**
   * Resets the redo request by setting the recorded turn number to -1. This indicates that there is
   * no more active redo request.
   */
  public void redoRequestReset() {
    this.redoRequestTurnNb = -1;
  }

  /*
  public void setZobristHashing(long zobristHashing) {
    this.zobristHashing = zobristHashing;
  }
    */

  /**
   * Sets the hint integers corresponding to the best move for the current Game State.
   *
   * @param newHintIntegers list of integers to add to the field hintIntegers
   */
  public void setHintIntegers(final List<Integer> newHintIntegers) {
    this.hintIntegers = newHintIntegers;
    notifyObservers(EventType.MOVE_HINT);
  }

  /**
   * Sets the field simplified zobrist hashing with the one in the parameters.
   *
   * @param hash hash corresponding to the simplified zobrist hashing
   */
  public void setSimplifiedZobristHashing(final long hash) {
    this.simplifiedZobristHashing = hash;
  }

  /** Changes threefoldstatus for the gamestate when it is observed. */
  public void activateThreefold() {
    this.threefoldRepetition = true;
  }

  /** White requests a draw. */
  public void doesWhiteWantsToDraw() {
    this.whiteWantsToDraw = true;
    if (!checkDrawAgreement()) {
      notifyObservers(EventType.WHITE_DRAW_PROPOSAL);
    }
  }

  /** Black requests a draw. */
  public void doesBlackWantsToDraw() {
    this.blackWantsToDraw = true;
    if (!checkDrawAgreement()) {
      notifyObservers(EventType.BLACK_DRAW_PROPOSAL);
    }
  }

  /** White cancels their draw request. */
  public void whiteCancelsDrawRequest() {
    this.whiteWantsToDraw = false;
    notifyObservers(EventType.WHITE_UNDRAW);
  }

  /** Black cancels their draw request. */
  public void blackCancelsDrawRequest() {
    this.blackWantsToDraw = false;
    notifyObservers(EventType.BLACK_UNDRAW);
  }

  /**
   * Ends the game and sends the according message depending on the player out of time and the lack
   * or not of material.
   *
   * @param isWhite boolean corresponding to the players color
   */
  public void playerOutOfTime(final boolean isWhite) {
    if (!this.getBoard().hasEnoughMaterialToMate(!isWhite)) {
      this.gameOver = true;
      debug(LOGGER, "End of game : Loss on time + insufficient material, Draw");
      if (isWhite) {
        notifyObservers(EventType.OUT_OF_TIME_WHITE);
      } else {
        notifyObservers(EventType.OUT_OF_TIME_BLACK);
      }
      notifyObservers(EventType.DRAW);
      return;
    }
    this.gameOver = true;
    if (isWhite) {
      debug(LOGGER, "End of game : Loss on time, Black won");
      notifyObservers(EventType.OUT_OF_TIME_WHITE);
      notifyObservers(EventType.WIN_BLACK);
    } else {
      debug(LOGGER, "End of game : Loss on time, White won");
      notifyObservers(EventType.OUT_OF_TIME_BLACK);
      notifyObservers(EventType.WIN_WHITE);
    }
  }

  /**
   * Checks if both players want to draw.
   *
   * @return true if there is a draw, meaning both players agreed to a draw
   */
  private boolean checkDrawAgreement() {
    if (hasWhiteRequestedDraw() && hasBlackRequestedDraw()) {
      this.gameOver = true;
      notifyObservers(EventType.DRAW_ACCEPTED);
      notifyObservers(EventType.DRAW);
      return true;
    }
    return false;
  }

  /** White decides to resign the game, making Black victorious. */
  public void whiteResigns() {
    this.whiteHasResigned = true;
    this.gameOver = true;
    notifyObservers(EventType.WHITE_RESIGNS);
    notifyObservers(EventType.WIN_BLACK);
  }

  /** Black decides to resign the game, making White victorious. */
  public void blackResigns() {
    this.blackHasResigned = true;
    this.gameOver = true;
    notifyObservers(EventType.BLACK_RESIGNS);
    notifyObservers(EventType.WIN_WHITE);
  }

  /**
   * Retrieves a boolean to indicate whether the white player has resigned.
   *
   * @return true if white has resigned, false otherwise
   */
  public boolean hasWhiteResigned() {
    return this.whiteHasResigned;
  }

  /**
   * Retrieves a boolean to indicate whether the black player has resigned.
   *
   * @return true if black has resigned, false otherwise
   */
  public boolean hasBlackResigned() {
    return this.blackHasResigned;
  }

  /**
   * Retrieves a boolean to indicate whether the black player has requested a draw.
   *
   * @return true if black has requested a draw, false otherwise
   */
  public boolean hasBlackRequestedDraw() {
    return this.blackWantsToDraw;
  }

  /**
   * Retrieves a boolean to indicate whether the white player has requested a draw.
   *
   * @return true if white has requested a draw, false otherwise
   */
  public boolean hasWhiteRequestedDraw() {
    return this.whiteWantsToDraw;
  }

  /**
   * Retrieves a boolean to indicate whether the white player has lost on time.
   *
   * @return true if white has lost on time at this state of the game, false otherwise
   */
  public boolean hasWhiteLostOnTime() {
    return this.whiteLosesOnTime;
  }

  /**
   * Retrieves a boolean to indicate whether the black player has lost on time.
   *
   * @return true if black lost on time at this state of the game; false otherwise
   */
  public boolean hasBlackLostOnTime() {
    return this.blackLosesOnTime;
  }

  /**
   * Retrieves a boolean to indicate whether the game is over.
   *
   * @return true if the match is over for this state of the , false otherwise
   */
  public boolean isGameOver() {
    return this.gameOver;
  }

  /**
   * Checks if fifty move rule has to be applied.
   *
   * @return true if fifty move rule is observed
   */
  public boolean isFiftyMoveRule() {
    return this.board.getNbFullMovesWithNoCaptureOrPawn() >= nMoveRule;
  }

  /** Fifty move rule is observed so change game status to 'Over', it's a draw. */
  public void applyFiftyMoveRule() {
    this.gameOver = true;
    notifyObservers(EventType.FIFTY_MOVE_RULE);
    notifyObservers(EventType.DRAW);
  }

  /**
   * Retrieves a boolean to indicate if the game is in a threefold repetition.
   *
   * @return true if the game has stated a threefold repetition rule, false otherwise
   */
  public boolean isThreefoldRepetition() {
    return this.threefoldRepetition;
  }

  /**
   * This method checks the ongoing or over status of the game. In summary, it will: Verify
   * checkMate, staleMate, draw by insufficient material, 50 move rule, draw by threefold
   * repetition, loss or draw on time, draw by mutual agreement, resigning. Will modify
   * this.isGameOver boolean attribute (or not) and send notification (or not) to observers if the
   * game is over. This method is called after every move is played.
   */
  public void checkGameStatus() {
    final Color currColor = this.isWhiteTurn() ? Color.WHITE : Color.BLACK;
    // Fifty Move rule
    if (isFiftyMoveRule()) {
      debug(LOGGER, "End of game : Fifty move rule, draw");
      applyFiftyMoveRule();
      return;
    }
    // Checkmate
    if (board.isCheckMate(currColor)) {
      this.gameOver = true;
      if (currColor == Color.WHITE) {
        debug(LOGGER, "End of game : Checkmate, Black won");
        notifyObservers(EventType.CHECKMATE_BLACK);
        notifyObservers(EventType.WIN_BLACK);
      } else {
        debug(LOGGER, "End of game : Checkmate, White won");
        notifyObservers(EventType.CHECKMATE_WHITE);
        notifyObservers(EventType.WIN_WHITE);
      }
      return;
    }
    // Stalemate
    if (board.isStaleMate(currColor, currColor)) {
      this.gameOver = true;
      debug(LOGGER, "End of game : Stale mate, Draw");
      notifyObservers(EventType.STALEMATE);
      notifyObservers(EventType.DRAW);
      return;
    }
    // Draw by insufficient material
    if (board.isDrawByInsufficientMaterial()) {
      debug(LOGGER, "End of game : Insufficient material, Draw");
      this.gameOver = true;
      notifyObservers(EventType.INSUFFICIENT_MATERIAL);
      notifyObservers(EventType.DRAW);
    }
    // Threefold repetition
    if (this.threefoldRepetition) {
      debug(LOGGER, "End of game : Threefold repetition, Draw");
      this.gameOver = true;
      notifyObservers(EventType.THREEFOLD_REPETITION);
      notifyObservers(EventType.DRAW);
    }
  }

  /**
   * Creates a deep copy of this GameState object. Copies all fields to ensure a completely
   * independent state.
   *
   * @return A new instance of GameState with the same state as the current object.
   */
  public GameState getCopy() {
    final GameState copy = new GameState();

    copy.board = this.board.getCopy();
    copy.whiteWantsToDraw = this.whiteWantsToDraw;
    copy.blackWantsToDraw = this.blackWantsToDraw;
    copy.whiteHasResigned = this.whiteHasResigned;
    copy.blackHasResigned = this.blackHasResigned;
    copy.whiteLosesOnTime = this.whiteLosesOnTime;
    copy.blackLosesOnTime = this.blackLosesOnTime;
    copy.gameOver = this.gameOver;
    copy.threefoldRepetition = this.threefoldRepetition;
    copy.fullTurnNumber = this.fullTurnNumber;
    copy.zobristHashing = this.zobristHashing;
    copy.simplifiedZobristHashing = this.simplifiedZobristHashing;

    return copy;
  }

  /**
   * Updates the current game state with the values from another game state.
   *
   * @param gameState The GameState from which to copy the values.
   */
  public void updateFrom(final GameState gameState) {
    this.board = gameState.getBoard();
    this.moveTimer = gameState.getMoveTimer();
    this.whiteWantsToDraw = gameState.hasWhiteRequestedDraw();
    this.blackWantsToDraw = gameState.hasBlackRequestedDraw();
    this.whiteHasResigned = gameState.hasWhiteResigned();
    this.blackHasResigned = gameState.hasBlackResigned();
    this.whiteLosesOnTime = gameState.hasWhiteLostOnTime();
    this.blackLosesOnTime = gameState.hasBlackLostOnTime();
    this.gameOver = gameState.isGameOver();
    this.threefoldRepetition = gameState.isThreefoldRepetition();
    this.fullTurnNumber = gameState.getFullTurn();
    this.zobristHashing = gameState.getZobristHashing();
    this.simplifiedZobristHashing = gameState.getSimplifiedZobristHashing();
  }
}
