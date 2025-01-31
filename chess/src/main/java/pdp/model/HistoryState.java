package pdp.model;

public class HistoryState {
  Board state;
  Move previousMove;

  public HistoryState(Board state, Move previousMove) {
    this.state = state;
    this.previousMove = previousMove;
  }

  public Board getState() {
    return state;
  }

  public Move getPreviousMove() {
    return previousMove;
  }
}
