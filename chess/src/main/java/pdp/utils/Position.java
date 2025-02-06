package pdp.utils;

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
}
