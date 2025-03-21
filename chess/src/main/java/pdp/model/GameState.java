package pdp.model;

import static pdp.utils.Logging.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pdp.events.EventType;
import pdp.events.Subject;
import pdp.model.board.Board;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.utils.Logging;
import pdp.utils.Timer;

/** State of the game. Can be observed by an observer. */
public class GameState extends Subject {
  private static final Logger LOGGER = Logger.getLogger(GameState.class.getName());
  public static int nMoveRule = 50;
  private Board board;
  private Timer moveTimer;
  private boolean isWhiteTurn;
  private boolean whiteWantsToDraw = false;
  private boolean blackWantsToDraw = false;
  private boolean whiteResigns = false;
  private boolean blackResigns = false;
  private int undoRequestTurnNumber = -1;
  private int redoRequestTurnNumber = -1;
  private boolean whiteLosesOnTime = false;
  private boolean blackLosesOnTime = false;
  private boolean isGameOver = false;
  private boolean threefoldRepetition = false;
  private int fullTurnNumber;
  private long zobristHashing;
  private long simplifiedZobristHashing;
  private List<Integer> hintIntegers = new ArrayList<>();

  static {
    Logging.configureLogging(LOGGER);
  }

  /** Creates a game state with default parameters. By default, blitz mode is not on */
  public GameState() {
    this.isGameOver = false;
    this.isWhiteTurn = true;
    this.board = new Board();
    this.moveTimer = null;
    this.fullTurnNumber = 0;
  }

  /**
   * Creates a game state with default parameter and the blitz.
   *
   * @param timer Timer for the blitz
   */
  public GameState(Timer timer) {
    this.isGameOver = false;
    this.isWhiteTurn = true;
    this.board = new Board();
    this.moveTimer = timer;
    this.fullTurnNumber = 0;
  }

  /**
   * Create a new GameState from a given board.
   *
   * @param board The board to use
   */
  public GameState(FileBoard board) {
    this.isGameOver = false;
    this.isWhiteTurn = board.isWhiteTurn();
    this.board = new Board(board);
    this.moveTimer = null;
    this.fullTurnNumber = board.header() != null ? board.header().playedMoves() : 0;
  }

  /**
   * Create a new GameState from a given board with a timer.
   *
   * @param board The board to use
   */
  public GameState(FileBoard board, Timer timer) {
    Logging.configureLogging(LOGGER);
    this.isGameOver = false;
    this.isWhiteTurn = board.isWhiteTurn();
    this.board = new Board(board);
    this.moveTimer = timer;
    this.fullTurnNumber = board.header() != null ? board.header().playedMoves() : 0;
  }

  /**
   * Retrieves a boolean corresponding to the color of the current player.
   *
   * @return true if the player is white, false if he is white
   */
  public boolean isWhiteTurn() {
    return this.isWhiteTurn;
  }

  /** Changes the color of the current player. */
  public void switchPlayerTurn() {
    this.isWhiteTurn = !(this.isWhiteTurn);
  }

  /**
   * Retrieves the current board.
   *
   * @return current board
   */
  public Board getBoard() {
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
    return this.undoRequestTurnNumber;
  }

  /**
   * Retrieves the turn number when the redo request was made.
   *
   * @return the turn number of the redo request
   */
  public int getRedoRequestTurnNumber() {
    return this.redoRequestTurnNumber;
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
    this.undoRequestTurnNumber = this.getFullTurn();
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
    this.undoRequestTurnNumber = -1;
  }

  /**
   * Requests to redo the last undone move by recording the current turn number. Notifies observers
   * about the redo proposal based on the player's turn.
   */
  public void redoRequest() {
    this.redoRequestTurnNumber = this.getFullTurn();
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
    this.redoRequestTurnNumber = -1;
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
  public void setHintIntegers(List<Integer> newHintIntegers) {
    this.hintIntegers = newHintIntegers;
    notifyObservers(EventType.MOVE_HINT);
  }

  /**
   * Sets the field simplified zobrist hashing with the one in the parameters.
   *
   * @param simplifiedZobristHashing hash corresponding to the simplified zobrist hashing
   */
  public void setSimplifiedZobristHashing(long simplifiedZobristHashing) {
    this.simplifiedZobristHashing = simplifiedZobristHashing;
  }

  /** Changes threefoldstatus for the gamestate when it is observed. */
  public void activateThreefold() {
    this.threefoldRepetition = true;
  }

  /** White requests a draw. */
  public void whiteWantsToDraw() {
    this.whiteWantsToDraw = true;
    if (!checkDrawAgreement()) {
      notifyObservers(EventType.WHITE_DRAW_PROPOSAL);
    }
  }

  /** Black requests a draw. */
  public void blackWantsToDraw() {
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
  public void playerOutOfTime(boolean isWhite) {
    if (!this.getBoard().getBoardRep().hasEnoughMaterialToMate(!isWhite)) {
      this.isGameOver = true;
      debug(LOGGER, "End of game : Loss on time + insufficient material, Draw");
      if (isWhite) {
        notifyObservers(EventType.OUT_OF_TIME_WHITE);
      } else {
        notifyObservers(EventType.OUT_OF_TIME_BLACK);
      }
      notifyObservers(EventType.DRAW);
      return;
    }
    this.isGameOver = true;
    if (isWhite) {
      debug(LOGGER, "End of game : Loss on time, Black won");
      notifyObservers(EventType.OUT_OF_TIME_WHITE);
      notifyObservers(EventType.WIN_BLACK);
      return;
    } else {
      debug(LOGGER, "End of game : Loss on time, White won");
      notifyObservers(EventType.OUT_OF_TIME_BLACK);
      notifyObservers(EventType.WIN_WHITE);
      return;
    }
  }

  /**
   * Checks if both players want to draw.
   *
   * @return true if there is a draw, meaning both players agreed to a draw
   */
  private boolean checkDrawAgreement() {
    if (hasWhiteRequestedDraw() && hasBlackRequestedDraw()) {
      this.isGameOver = true;
      notifyObservers(EventType.DRAW_ACCEPTED);
      notifyObservers(EventType.DRAW);
      return true;
    }
    return false;
  }

  /** White decides to resign the game, making Black victorious. */
  public void whiteResigns() {
    this.whiteResigns = true;
    this.isGameOver = true;
    notifyObservers(EventType.WHITE_RESIGNS);
    notifyObservers(EventType.WIN_BLACK);
  }

  /** Black decides to resign the game, making White victorious. */
  public void blackResigns() {
    this.blackResigns = true;
    this.isGameOver = true;
    notifyObservers(EventType.BLACK_RESIGNS);
    notifyObservers(EventType.WIN_WHITE);
  }

  /**
   * Retrieves a boolean to indicate whether the white player has resigned.
   *
   * @return true if white has resigned, false otherwise
   */
  public boolean hasWhiteResigned() {
    return this.whiteResigns;
  }

  /**
   * Retrieves a boolean to indicate whether the black player has resigned.
   *
   * @return true if black has resigned, false otherwise
   */
  public boolean hasBlackResigned() {
    return this.blackResigns;
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
    return this.isGameOver;
  }

  /**
   * Checks if fifty move rule has to be applied.
   *
   * @return true if fifty move rule is observed
   */
  public boolean isFiftyMoveRule() {
    return this.board.getNbMovesWithNoCaptureOrPawn() >= nMoveRule;
  }

  /** Fifty move rule is observed so change game status to 'Over', it's a draw. */
  public void applyFiftyMoveRule() {
    this.isGameOver = true;
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
    Color currColor = this.isWhiteTurn() ? Color.WHITE : Color.BLACK;
    // Fifty Move rule
    if (isFiftyMoveRule()) {
      debug(LOGGER, "End of game : Fifty move rule, draw");
      applyFiftyMoveRule();
      return;
    }
    // Checkmate
    if (board.getBoardRep().isCheckMate(currColor)) {
      this.isGameOver = true;
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
    if (board.getBoardRep().isStaleMate(currColor, currColor)) {
      this.isGameOver = true;
      debug(LOGGER, "End of game : Stale mate, Draw");
      notifyObservers(EventType.STALEMATE);
      notifyObservers(EventType.DRAW);
      return;
    }
    // Draw by insufficient material
    if (board.getBoardRep().isDrawByInsufficientMaterial()) {
      debug(LOGGER, "End of game : Insufficient material, Draw");
      this.isGameOver = true;
      notifyObservers(EventType.INSUFFICIENT_MATERIAL);
      notifyObservers(EventType.DRAW);
    }
    // Threefold repetition
    if (this.threefoldRepetition) {
      debug(LOGGER, "End of game : Threefold repetition, Draw");
      this.isGameOver = true;
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
    GameState copy = new GameState();

    copy.board = this.board.getCopy();
    copy.isWhiteTurn = this.isWhiteTurn;
    copy.whiteWantsToDraw = this.whiteWantsToDraw;
    copy.blackWantsToDraw = this.blackWantsToDraw;
    copy.whiteResigns = this.whiteResigns;
    copy.blackResigns = this.blackResigns;
    copy.whiteLosesOnTime = this.whiteLosesOnTime;
    copy.blackLosesOnTime = this.blackLosesOnTime;
    copy.isGameOver = this.isGameOver;
    copy.threefoldRepetition = this.threefoldRepetition;
    copy.fullTurnNumber = this.fullTurnNumber;
    copy.zobristHashing = this.zobristHashing;
    copy.simplifiedZobristHashing = this.simplifiedZobristHashing;

    /* if (this.moveTimer != null) {
        copy.moveTimer = this.moveTimer.getCopy();
    } else {
        copy.moveTimer = null;
    } */

    return copy;
  }

  /**
   * Updates the current game state with the values from another game state.
   *
   * @param gameState The GameState from which to copy the values.
   */
  public void updateFrom(GameState gameState) {
    this.board = gameState.getBoard();
    this.moveTimer = gameState.getMoveTimer();
    this.isWhiteTurn = gameState.isWhiteTurn();
    this.whiteWantsToDraw = gameState.hasWhiteRequestedDraw();
    this.blackWantsToDraw = gameState.hasBlackRequestedDraw();
    this.whiteResigns = gameState.hasWhiteResigned();
    this.blackResigns = gameState.hasBlackResigned();
    this.whiteLosesOnTime = gameState.hasWhiteLostOnTime();
    this.blackLosesOnTime = gameState.hasBlackLostOnTime();
    this.isGameOver = gameState.isGameOver();
    this.threefoldRepetition = gameState.isThreefoldRepetition();
    this.fullTurnNumber = gameState.getFullTurn();
    this.zobristHashing = gameState.getZobristHashing();
    this.simplifiedZobristHashing = gameState.getSimplifiedZobristHashing();
  }

  /**
   * Sets the current player with the boolean in parameter.
   *
   * @param white true if the player is white, false otherwise
   */
  public void setPlayer(boolean white) {
    this.isWhiteTurn = white;
  }
}
