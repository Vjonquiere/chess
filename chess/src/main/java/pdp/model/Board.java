package pdp.model;

import java.util.List;

public class Board {
  BoardRepresentation board;
  boolean isWhite;
  byte enPassant;
  boolean whiteShortCastle;
  boolean blackShortCastle;
  boolean whiteLongCastle;
  boolean blackLongCastle;

  public Board() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public List<Move> getAvailableMoves() {
    // TODO
    throw new UnsupportedOperationException();
  }

  public boolean makeMove(Move move) {
    // TODO
    throw new UnsupportedOperationException();
  }

  public Board getCopy() {
    // TODO
    throw new UnsupportedOperationException();
  }
}
