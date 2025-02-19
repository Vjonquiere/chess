package pdp.model;

import static pdp.utils.Logging.DEBUG;

import java.util.logging.Logger;
import pdp.events.EventType;
import pdp.events.Subject;
import pdp.model.board.Board;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.utils.Logging;
import pdp.utils.Timer;

public class GameState extends Subject {
  private static final Logger LOGGER = Logger.getLogger(GameState.class.getName());

  private Board board;
  private Timer moveTimer;
  private boolean isWhiteTurn;
  private boolean whiteWantsToDraw;
  private boolean blackWantsToDraw;
  private boolean whiteResigns;
  private boolean blackResigns;
  private boolean whiteLosesOnTime;
  private boolean blackLosesOnTime;
  private boolean isGameOver;
  private boolean threefoldRepetition;
  private int fullTurnNumber;
  private long zobristHashing;
  private long simplifiedZobristHashing;

  static {
    Logging.configureLogging(LOGGER);
  }

  // By default, blitz mode is not on
  public GameState() {
    this.isGameOver = false;
    this.isWhiteTurn = true;
    this.whiteResigns = false;
    this.blackResigns = false;
    this.whiteWantsToDraw = false;
    this.blackWantsToDraw = false;
    this.whiteLosesOnTime = false;
    this.blackLosesOnTime = false;
    this.threefoldRepetition = false;
    this.board = new Board();
    this.moveTimer = null;
    this.fullTurnNumber = 0;
  }

  public GameState(Timer timer) {
    this.isGameOver = false;
    this.isWhiteTurn = true;
    this.whiteResigns = false;
    this.blackResigns = false;
    this.whiteWantsToDraw = false;
    this.blackWantsToDraw = false;
    this.whiteLosesOnTime = false;
    this.blackLosesOnTime = false;
    this.board = new Board();
    this.moveTimer = timer;
    this.fullTurnNumber = 0;
  }

  /**
   * Create a new GameState from a given board
   *
   * @param board The board to use
   */
  public GameState(FileBoard board) {
    this.isGameOver = false;
    this.isWhiteTurn = board.isWhiteTurn();
    this.whiteResigns = false;
    this.blackResigns = false;
    this.whiteWantsToDraw = false;
    this.blackWantsToDraw = false;
    this.whiteLosesOnTime = false;
    this.blackLosesOnTime = false;
    this.threefoldRepetition = false;
    this.board = new Board(board);
    this.moveTimer = null;
    this.fullTurnNumber = 0;
  }

  public boolean isWhiteTurn() {
    return this.isWhiteTurn;
  }

  public void switchPlayerTurn() {
    this.isWhiteTurn = !(this.isWhiteTurn);
  }

  public Board getBoard() {
    return board;
  }

  public Timer getMoveTimer() {
    return this.moveTimer;
  }

  public long getZobristHashing() {
    return this.zobristHashing;
  }

  public long getSimplifiedZobristHashing() {
    return this.simplifiedZobristHashing;
  }

  public int getFullTurn() {
    return this.fullTurnNumber;
  }

  public void incrementsFullTurn() {
    this.fullTurnNumber += 1;
  }

  /*
  public void setZobristHashing(long zobristHashing) {
    this.zobristHashing = zobristHashing;
  }
    */

  public void setSimplifiedZobristHashing(long simplifiedZobristHashing) {
    this.simplifiedZobristHashing = simplifiedZobristHashing;
  }

  /** Change threefoldstatus for the gamestate when it is observed */
  public void activateThreefold() {
    this.threefoldRepetition = true;
  }

  /** White requests a draw */
  public void whiteWantsToDraw() {
    this.whiteWantsToDraw = true;
    if (!checkDrawAgreement()) {
      notifyObservers(EventType.WHITE_DRAW_PROPOSAL);
    }
  }

  /** Black requests a draw */
  public void blackWantsToDraw() {
    this.blackWantsToDraw = true;
    if (!checkDrawAgreement()) {
      notifyObservers(EventType.BLACK_DRAW_PROPOSAL);
    }
  }

  /** White cancels their draw request */
  public void whiteCancelsDrawRequest() {
    this.whiteWantsToDraw = false;
    notifyObservers(EventType.WHITE_UNDRAW);
  }

  /** Black cancels their draw request */
  public void blackCancelsDrawRequest() {
    this.blackWantsToDraw = false;
    notifyObservers(EventType.BLACK_UNDRAW);
  }

  /**
   * Checks if both players want to draw
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

  /** White decides to resign the game, making Black victorious */
  public void whiteResigns() {
    this.whiteResigns = true;
    this.isGameOver = true;
    notifyObservers(EventType.WIN_BLACK);
  }

  /** Black decides to resign the game, making White victorious */
  public void blackResigns() {
    this.blackResigns = true;
    this.isGameOver = true;
    notifyObservers(EventType.WIN_WHITE);
  }

  /**
   * @return true if white has resigned, false otherwise
   */
  public boolean hasWhiteResigned() {
    return this.whiteResigns;
  }

  /**
   * @return true if black has resigned, false otherwise
   */
  public boolean hasBlackResigned() {
    return this.blackResigns;
  }

  /**
   * @return true if black has requested a draw, false otherwise
   */
  public boolean hasBlackRequestedDraw() {
    return this.blackWantsToDraw;
  }

  /**
   * @return true if white has requested a draw, false otherwise
   */
  public boolean hasWhiteRequestedDraw() {
    return this.whiteWantsToDraw;
  }

  /**
   * Checks if the current player loses on time
   *
   * @return true if the current player runs out of time for his move (blitz mode)
   */
  public boolean playerLosesOnTime() {
    if (this.moveTimer != null && this.moveTimer.getTimeRemaining() == 0) {
      if (this.isWhiteTurn) {
        this.whiteLosesOnTime = true;
        this.isGameOver = true;
        notifyObservers(EventType.WIN_BLACK);
        return true;
      } else {
        this.blackLosesOnTime = true;
        this.isGameOver = true;
        notifyObservers(EventType.WIN_WHITE);
        return true;
      }
    }
    return false;
  }

  /**
   * @return true if white lost on time at this state of the game, false otherwise
   */
  public boolean hasWhiteLostOnTime() {
    return this.whiteLosesOnTime;
  }

  /**
   * @return true if black lost on time at this state of the game; false otherwise
   */
  public boolean hasBlackLostOnTime() {
    return this.blackLosesOnTime;
  }

  /**
   * @return true if the match is over for this state of the , false otherwise
   */
  public boolean isGameOver() {
    return this.isGameOver;
  }

  /**
   * Checks if fifty move rule has to be applied
   *
   * @return true if fifty move rule is observed
   */
  public boolean isFiftyMoveRule() {
    return this.board.getNbMovesWithNoCaptureOrPawn() >= 50;
  }

  /** Fifty move rule is observed so change game status to 'Over', it's a draw */
  public void applyFiftyMoveRule() {
    this.isGameOver = true;
    notifyObservers(EventType.DRAW);
  }

  /**
   * @return true if the game has stated a threefold repetiton rule, false otherwise
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
    boolean currPlayerWhite = this.isWhiteTurn();
    // Draw by agreement
    if (checkDrawAgreement()) {
      DEBUG(LOGGER, "End of game  Dray by mutual agreement");
      return;
    }
    // White resigns
    if (hasWhiteResigned()) {
      DEBUG(LOGGER, "End of game : White resigned, Black won");
      whiteResigns();
      return;
    }
    // Black resigns
    if (hasBlackResigned()) {
      DEBUG(LOGGER, "End of game : Black resigned, White won");
      blackResigns();
      return;
    }
    // Loss on time
    if (playerLosesOnTime()) {
      // if insufficient material for enemy then draw else win
      if (board.getBoardRep().hasEnoughMaterialToMate(currPlayerWhite)) {
        this.isGameOver = true;
        DEBUG(LOGGER, "End of game : Loss on time + insufficient material, Draw");
        notifyObservers(EventType.DRAW);
        return;
      }
      this.isGameOver = true;
      if (currPlayerWhite) {
        DEBUG(LOGGER, "End of game : Loss on time, Black won");
        notifyObservers(EventType.WIN_BLACK);
        return;
      } else {
        DEBUG(LOGGER, "End of game : Loss on time, White won");
        notifyObservers(EventType.WIN_WHITE);
        return;
      }
    }
    // Fifty Move rule
    if (isFiftyMoveRule()) {
      DEBUG(LOGGER, "End of game : Fifty move rule, draw");
      applyFiftyMoveRule();
      return;
    }
    // Checkmate
    if (board.getBoardRep().isCheckMate(currColor)) {
      this.isGameOver = true;
      if (currColor == Color.WHITE) {
        DEBUG(LOGGER, "End of game : Checkmate, Black won");
        notifyObservers(EventType.WIN_BLACK);
      } else {
        DEBUG(LOGGER, "End of game : Checkmate, White won");
        notifyObservers(EventType.WIN_WHITE);
      }
      return;
    }
    // Stalemate
    if (board.getBoardRep().isStaleMate(currColor, currColor)) {
      this.isGameOver = true;
      DEBUG(LOGGER, "End of game : Stale mate, Draw");
      notifyObservers(EventType.DRAW);
      return;
    }
    // Draw by insufficient material
    if (board.getBoardRep().isDrawByInsufficientMaterial()) {
      DEBUG(LOGGER, "End of game : Insufficient material, Draw");
      this.isGameOver = true;
      notifyObservers(EventType.DRAW);
    }
    // Threefold repetition
    if (this.threefoldRepetition) {
      DEBUG(LOGGER, "End of game : Threefold repetition, Draw");
      this.isGameOver = true;
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
}
