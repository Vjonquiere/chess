package pdp.utils;

import java.util.Objects;
import java.util.logging.Logger;

public class Position {
  private static final Logger LOGGER = Logger.getLogger(Position.class.getName());
  int x;
  int y;

  public Position(int y, int x) {
    Logging.configureLogging(LOGGER);
    this.y = y;
    this.x = x;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Position position = (Position) obj;
    return x == position.getX() && y == position.getY();
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y); // hashCode est souvent nécessaire en parallèle avec equals
  }
}
