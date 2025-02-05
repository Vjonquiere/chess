package pdp.utils;

import java.util.logging.Logger;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    Position position = (Position) o;
    return (position.getX() == this.x) && (position.getY() == this.y);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this);
  }
}
