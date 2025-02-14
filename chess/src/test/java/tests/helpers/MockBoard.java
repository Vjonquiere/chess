package tests.helpers;

import pdp.model.Board;
import pdp.model.BoardRepresentation;

public class MockBoard extends Board {
  @Override
  public BoardRepresentation getBoard() {
    return new DummyBoardRepresentation(); // Not a BitboardRepresentation
  }
}
