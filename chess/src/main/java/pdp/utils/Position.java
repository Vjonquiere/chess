package pdp.utils;

import java.util.Objects;
import java.util.logging.Logger;

public class Position {
  private static final Logger LOGGER = Logger.getLogger(Position.class.getName());
  int x;
  int y;

  static {
    Logging.configureLogging(LOGGER);
  }

  public Position(int x, int y) {
    this.y = y;
    this.x = x;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean isValid() {
    return getX() <= 7 && getX() >= 0 && getY() <= 7 && getY() >= 0;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Position position = (Position) obj;
    return x == position.getX() && y == position.getY();
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y); // hashCode est souvent nécessaire en parallèle avec equals
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
