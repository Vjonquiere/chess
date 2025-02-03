package pdp.model;

import pdp.utils.Position;

public class Move {
  Position source;
  Position dest;
  Piece piece;
  boolean isTake;
  boolean isCheck;
  boolean isCheckMate;

  public Move() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public static Move fromString(String move) {
    // TODO
    throw new UnsupportedOperationException("Method not implemented");
  }

  public Position getSource() {
    return source;
  }

  public Position getDest() {
    return dest;
  }

  public Piece getPiece() {
    return piece;
  }

  public boolean isTake() {
    return isTake;
  }

  public boolean isCheck() {
    return isCheck;
  }

  public boolean isCheckMate() {
    return isCheckMate;
  }

  @Override
  public String toString() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
