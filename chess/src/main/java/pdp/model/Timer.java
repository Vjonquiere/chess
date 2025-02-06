package pdp.model;

import java.util.logging.Logger;
import pdp.utils.Logging;

public class Timer {
  private static final Logger LOGGER = Logger.getLogger(Timer.class.getName());

  // TODO

  public Timer(int time) {
    Logging.configureLogging(LOGGER);
  }

  public int timeRemaining() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public String timeRemainingString() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
