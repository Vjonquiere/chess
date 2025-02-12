package pdp.model;

import pdp.events.EventType;
import pdp.events.Subject;

public class GameState extends Subject {
  private Board board;
  private Timer moveTimer;
  private History history;
  private boolean isWhiteTurn;
  private boolean whiteWantsToDraw;
  private boolean blackWantsToDraw;
  private boolean whiteResigns;
  private boolean blackResigns;
  private boolean whiteLosesOnTime;
  private boolean blackLosesOnTime;
  private boolean isGameOver;
  private int fullTurnNumber;

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
    // this.history = new History();  When history is implemented
    this.history = null;
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
    // this.history = new History();  When history is implemented
    this.history = null;
    this.board = new Board();
    this.moveTimer = timer;
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

  public History getHistory() {
    return history;
  }

  public Timer getMoveTimer() {
    return this.moveTimer;
  }

  public int getFullTurn() {
    return this.fullTurnNumber;
  }

  public void incrementsFullTurn() {
    this.fullTurnNumber += 1;
  }

  public void whiteWantsToDraw() {
    this.whiteWantsToDraw = true;
    if (!checkDrawAgreement()) {
      notifyObservers(EventType.WHITE_DRAW_PROPOSAL);
    }
  }

  public void blackWantsToDraw() {
    this.blackWantsToDraw = true;
    if (!checkDrawAgreement()) {
      notifyObservers(EventType.BLACK_DRAW_PROPOSAL);
    }
  }

  public void whiteCancelsDrawRequest() {
    this.whiteWantsToDraw = false;
    notifyObservers(EventType.WHITE_UNDRAW);
  }

  public void blackCancelsDrawRequest() {
    this.blackWantsToDraw = false;
    notifyObservers(EventType.BLACK_UNDRAW);
  }

  private boolean checkDrawAgreement() {
    if (hasWhiteRequestedDraw() && hasBlackRequestedDraw()) {
      this.isGameOver = true;
      notifyObservers(EventType.DRAW_ACCEPTED);
      notifyObservers(EventType.DRAW);
      return true;
    }
    return false;
  }

  public void whiteResigns() {
    this.whiteResigns = true;
    this.isGameOver = true;
    notifyObservers(EventType.WIN_BLACK);
  }

  public void blackResigns() {
    this.blackResigns = true;
    this.isGameOver = true;
    notifyObservers(EventType.WIN_WHITE);
  }

  public boolean hasWhiteResigned() {
    return this.whiteResigns;
  }

  public boolean hasBlackResigned() {
    return this.blackResigns;
  }

  public boolean hasBlackRequestedDraw() {
    return this.blackWantsToDraw;
  }

  public boolean hasWhiteRequestedDraw() {
    return this.whiteWantsToDraw;
  }

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

  public boolean hasWhiteLostOnTime() {
    return this.whiteLosesOnTime;
  }

  public boolean hasBlackLostOnTime() {
    return this.blackLosesOnTime;
  }

  public boolean isGameOver() {
    return this.isGameOver;
  }

  public boolean isFiftyMoveRule() {
    return this.board.getNbMovesWithNoCaptureOrPawn() >= 50;
  }

  public void applyFiftyMoveRule() {
    this.isGameOver = true;
    notifyObservers(EventType.DRAW);
  }

  /**
   * This method checks the ongoing or over status of the game. In summary, it will: Verify
   * checkMate, staleMate, draw by insufficient material, 50 move rule, draw by threefold
   * repetition, loss or draw on time, draw by mutual agreement, resigning. Will modify
   * this.isGameOver boolean attribute (or not) so that Game will call this.isGameOver() to know the
   * status of the game.
   */
  public void checkGameStatus() {
    Color currColor = this.isWhiteTurn() ? Color.WHITE : Color.BLACK;
    boolean currPlayerWhite = this.isWhiteTurn();
    // Draw by agreement
    if (checkDrawAgreement()) {
      return;
    }
    // White resigns
    if (hasWhiteResigned()) {
      whiteResigns();
      return;
    }
    // Black resigns
    if (hasBlackResigned()) {
      blackResigns();
      return;
    }
    // Loss on time
    if (playerLosesOnTime()) {
      // if insufficient material for enemy then draw else win
      if (board.getBoard().hasEnoughMaterialToMate(currPlayerWhite)) {
        this.isGameOver = true;
        notifyObservers(EventType.DRAW);
        return;
      }
      this.isGameOver = true;
      if (currPlayerWhite) {
        notifyObservers(EventType.WIN_BLACK);
        return;
      } else {
        notifyObservers(EventType.WIN_WHITE);
        return;
      }
    }
    // Fifty Move rule
    if (isFiftyMoveRule()) {
      applyFiftyMoveRule();
      return;
    }
    // Checkmate
    if (board.getBoard().isCheckMate(currColor)) {
      this.isGameOver = true;
      if (currColor == Color.WHITE) {
        notifyObservers(EventType.WIN_BLACK);
      } else {
        notifyObservers(EventType.WIN_WHITE);
      }
      return;
    }
    // Stalemate
    if (board.getBoard().isStaleMate(currColor, currColor)) {
      this.isGameOver = true;
      notifyObservers(EventType.DRAW);
      return;
    }
    // Draw by insufficient material
    if (board.getBoard().isDrawByInsufficientMaterial()) {
      this.isGameOver = true;
      notifyObservers(EventType.DRAW);
    }
    // Threefold repetition
  }
}
