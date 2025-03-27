package pdp.utils;

import static pdp.utils.Logging.debug;

import java.util.logging.Logger;

/**
 * Utility class creating a timer. There is the possibility to add a callback, to make an action if
 * the timer is over.
 */
public class Timer implements Runnable {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(Timer.class.getName());

  /** Duration of the timer, in milliseconds. */
  private final long duration;

  /** Remaining time in the timer. */
  private long remaining;

  /** Boolean to indicate whether the timer is currently running. */
  private boolean running;

  /** Thread to run the timer on. */
  private Thread thread;

  /** Callback when the timer is over. */
  private Runnable timeOverCallback;

  /** Time when the timer was started. */
  private long startTime;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Creates a timer with the time and callback given in parameters.
   *
   * @param time duration of the timer
   * @param timeOverCallback callback when the timer is over.
   */
  public Timer(final long time, final Runnable timeOverCallback) {
    this.duration = time;
    this.remaining = this.duration;
    this.timeOverCallback = timeOverCallback;
  }

  /**
   * Creates a timer with the time given in parameters.
   *
   * @param time duration of the timer
   */
  public Timer(final long time) {
    this.duration = time;
    this.remaining = this.duration;
  }

  /**
   * Sets the callback to be executed when the timer reaches zero.
   *
   * @param timeOverCallback The Runnable to be executed when the time is over.
   */
  public void setCallback(final Runnable timeOverCallback) {
    this.timeOverCallback = timeOverCallback;
  }

  /**
   * Run the timer. This method is run in a separate thread. When remaining time is 0, the
   * timeOverCallback is called.
   */
  @Override
  public void run() {
    this.running = true;
    this.startTime = System.currentTimeMillis();
    try {
      Thread.sleep(this.duration);
      this.remaining = 0;
      if (running && this.timeOverCallback != null) {
        this.timeOverCallback.run();
      }
    } catch (Exception ignored) {
      // System.err.println(e.getMessage());
    }
    running = false;
  }

  /** Creates and starts the timer thread. */
  public synchronized void start() {
    if (!running) {
      this.remaining = this.duration;
      this.thread = new Thread(this);
      this.thread.start();
    }
  }

  /** Stops the timer, updates the remaining time, and interrupts the timer thread if running. */
  public synchronized void stop() {
    this.running = false;
    this.remaining -= System.currentTimeMillis() - this.startTime;
    debug(LOGGER, "Remaining time at stop: " + this.remaining);
    if (this.thread != null) {
      this.thread.interrupt();
    }
  }

  /**
   * Returns the time remaining on the timer.
   *
   * @return the time remaining in milliseconds.
   */
  public long getTimeRemaining() {
    if (running) {
      final long elapsed = System.currentTimeMillis() - this.startTime;
      return Math.max(this.remaining - elapsed, 0);
    } else {
      return this.remaining;
    }
  }

  /**
   * Returns a string representation of the time remaining on the timer. If the duration is less
   * than an hour, the format is mm:ss. If the duration is an hour or more, the format is hh:mm:ss.
   *
   * @return A string representation of the time remaining on the timer.
   */
  public String getTimeRemainingString() {
    final long totalSeconds = getTimeRemaining() / 1000;

    if (this.duration < 3_600_000) {
      final long minutes = totalSeconds / 60;
      final long seconds = totalSeconds % 60;
      return String.format("%02d:%02d", minutes, seconds);
    } else {
      final long hours = totalSeconds / 3600;
      final long minutes = totalSeconds % 3600 / 60;
      final long seconds = totalSeconds % 60;
      return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
  }
}
