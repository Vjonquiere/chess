package pdp.utils;

/** Relative position depending on x and y coordinates. */
public record Position(int x, int y) {

  /**
   * Retrieves a boolean to indicate if the position is valid depending on the size of a chess board
   * (8x8).
   *
   * @return validity on a chess board.
   */
  public boolean isValid() {
    return x() <= 7 && x() >= 0 && y() <= 7 && y() >= 0;
  }

  @Override
  public String toString() {
    return "Position [x=" + x + ", y=" + y + "]";
  }

  /**
   * Creates a deep copy of this Timer object.
   *
   * @return A new Position instance with the same state as the current object.
   */
  public Position getCopy() {
    return new Position(this.x, this.y);
  }
}
