package pdp.model;

import pdp.events.Subject;

public class GameState extends Subject {
  private Board board;
  private Timer whiteTimer;
  private Timer blackTimer;
  private History history;
  private boolean isWhiteTurn;

  // By default, blitz mode is not on
  public GameState() {
    this.isWhiteTurn = true;
    this.history = new History();
    this.board = new Board();
    this.whiteTimer = null;
    this.blackTimer = null;
  }

  public GameState(boolean isBlitzModeOn) {
    this.isWhiteTurn = true;
    this.history = new History();
    this.board = new Board();
    this.whiteTimer = new Timer();
    this.blackTimer = new Timer();
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

  public Timer getWhiteTimer() {
    return whiteTimer;
  }

  public Timer getBlackTimer() {
    return blackTimer;
  }
}
