package tests.helpers;

import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;

public class MockBoard extends Board {
  @Override
  public BoardRepresentation getBoardRep() {
    return new DummyBoardRepresentation(); // Not a BitboardRepresentation
  }
}
