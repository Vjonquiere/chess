package pdp.utils;

/** Relative position depending on x and y coordinates. */
public record Position(int x, int y) {

  /**
   * Creates a position.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public Position {}

  /**
   * Retrieves the x coordinate.
   *
   * @return x coordinate
   */
  @Override
  public int x() {
    return x;
  }

  /**
   * Retrieves the y coordinate.
   *
   * @return y coordinate
   */
  @Override
  public int y() {
    return y;
  }

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
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Position position = (Position) obj;
    return x == position.x() && y == position.y();
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
