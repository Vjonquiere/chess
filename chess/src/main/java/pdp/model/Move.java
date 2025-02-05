package pdp.model;

import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

public class Move {
  private static final Logger LOGGER = Logger.getLogger(Move.class.getName());
  Position source;
  Position dest;
  Piece piece;
  boolean isTake;
  boolean isCheck;
  boolean isCheckMate;

  public Move() {
    Logging.configureLogging(LOGGER);
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
