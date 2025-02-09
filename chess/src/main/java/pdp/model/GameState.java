package pdp.model;

import pdp.events.Subject;

public class GameState extends Subject {
  private Board board;
  private Timer moveTimer;
  private History history;
  private boolean isWhiteTurn;

  // By default, blitz mode is not on
  public GameState() {
    this.isWhiteTurn = true;
    // this.history = new History();  When history is implemented
    this.history = null;
    this.board = new Board();
    this.moveTimer = null;
  }

  public GameState(Timer timer) {
    this.isWhiteTurn = true;
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
}
