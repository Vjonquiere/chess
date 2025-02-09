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

  public void whiteWantsToDraw() {
    this.whiteWantsToDraw = true;
    checkDrawAgreement();
  }

  public void blackWantsToDraw() {
    this.blackWantsToDraw = true;
    checkDrawAgreement();
  }

  public void whiteCancelsDrawRequest() {
    this.whiteWantsToDraw = false;
  }

  public void blackCancelsDrawRequest() {
    this.blackWantsToDraw = false;
  }

  private void checkDrawAgreement() {
    if (whiteWantsToDraw && blackWantsToDraw) {
      // TO DO
      System.out.println("Game drawn by mutual agreement!");
      this.isGameOver = true;
      notifyObservers(EventType.GAME_OVER);
    }
  }

  public void whiteResigns() {
    this.whiteResigns = true;
    this.isGameOver = true;
    notifyObservers(EventType.GAME_OVER);
  }

  public void blackResigns() {
    this.blackResigns = true;
    this.isGameOver = true;
    notifyObservers(EventType.GAME_OVER);
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

  public void playerLosesOnTime() {
    if (this.moveTimer != null && this.moveTimer.getTimeRemaining() == 0) {
      if (this.isWhiteTurn) {
        this.whiteLosesOnTime = true;
        this.isGameOver = true;
        notifyObservers(EventType.GAME_OVER);
      } else {
        this.blackLosesOnTime = true;
        this.isGameOver = true;
        notifyObservers(EventType.GAME_OVER);
      }
    }
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
    notifyObservers(EventType.GAME_OVER);
  }
}
